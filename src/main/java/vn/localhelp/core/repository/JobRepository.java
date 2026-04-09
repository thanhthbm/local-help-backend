package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.util.constant.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
  long countByHelperIdAndJobStatus(Long helperId, JobStatus status);

  @Query("SELECT j FROM Job j "
      + "WHERE j.jobStatus = :status "
      + "AND j.creator.firebaseUid != :currentUid")
  Page<Job> findByJobStatus(@Param("status") JobStatus status, String currentUid, Pageable pageable);
  long countByJobStatus(JobStatus status);
}
