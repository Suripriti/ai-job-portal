package jar.service;

import jar.entity.Job;
import jar.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Create Job
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    // Get All Jobs (Pagination Supported)
    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    // Get Single Job
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    // Delete Job
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}