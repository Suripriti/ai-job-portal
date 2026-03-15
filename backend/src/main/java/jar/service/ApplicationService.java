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

        // Validate resume file
        if (resumeFile == null || resumeFile.isEmpty()) {
            throw new RuntimeException("Resume file is empty!");
        }

        // Fetch Job
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            throw new RuntimeException("Job not found!");
        }
        Job job = jobOptional.get();

        // Fetch User
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found!");
        }
        User user = userOptional.get();

        // Prevent duplicate application
        if (applicationRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new RuntimeException("You have already applied to this job.");
        }

        // Create uploads directory
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Generate unique file name
        String fileName = UUID.randomUUID() + "_" + resumeFile.getOriginalFilename();
        String filePath = uploadDir + File.separator + fileName;

        // Save resume file
        File destinationFile = new File(filePath);
        resumeFile.transferTo(destinationFile);

        // Default score
        Double score = 0.0;

        // Call AI Service
        try {
            RestTemplate restTemplate = new RestTemplate();
            String aiUrl = "http://localhost:8000/score";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Request body
            Map<String, String> body = Map.of(
                    "resumePath", filePath,
                    "jobDescription", job.getDescription()
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(aiUrl, request, Map.class);

            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("score")) {
                score = Double.parseDouble(responseBody.get("score").toString());
            }

            System.out.println("AI SCORE RECEIVED: " + score);

        } catch (Exception e) {
            System.out.println("AI service not running. Default score = 0");
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