package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.util.constant.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {
  long countByHelperIdAndJobStatus(Long helperId, JobStatus status);

  Page<Job> findByJobStatus(JobStatus jobStatus, Pageable pageable);
}
