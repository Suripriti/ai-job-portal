package jar.service;

import jar.entity.Application;
import jar.entity.Job;
import jar.entity.User;
import jar.repository.ApplicationRepository;
import jar.repository.JobRepository;
import jar.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public Application applyJob(Long jobId, Long userId, MultipartFile resumeFile) throws IOException {

        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file is empty!");
        }

        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found!");
        }
        Job job = jobOptional.get();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
        User user = userOptional.get();

        // Check for duplicate application
        if (applicationRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "You have already applied to this job."
            );
        }

        // Create uploads folder
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Save resume file
        String fileName = UUID.randomUUID() + "_" + resumeFile.getOriginalFilename();
        String filePath = uploadDir + File.separator + fileName;
        File destinationFile = new File(filePath);
        resumeFile.transferTo(destinationFile);

        // Initialize AI score
        Double score = 0.0;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String aiUrl = "https://resume-ai-service.onrender.com/analyze";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("job_description", job.getDescription());
            body.add("resume", new FileSystemResource(destinationFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(aiUrl, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.get("similarity_score") != null) {
                score = Double.parseDouble(responseBody.get("similarity_score").toString());
            }

            System.out.println("AI SCORE RECEIVED: " + score);

        } catch (Exception e) {
            System.out.println("AI service error: " + e.getMessage());
        }

        // Save application
        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setResumePath(filePath);
        application.setScore(score);

        return applicationRepository.save(application);
    }
}