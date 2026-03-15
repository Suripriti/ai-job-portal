package jar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jar.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}
