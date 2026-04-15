package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.JobApplication;
import vn.localhelp.core.util.constant.JobProgress;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByJobIdAndHelperId(Long jobId, Long helperId);
    Page<JobApplication> findByHelperId(Long helperId, Pageable pageable);
    Optional<JobApplication> findByJobIdAndHelperId(Long jobId, Long helperId);
    List<JobApplication> findByJobIdAndCurrentProgress(Long jobId, JobProgress currentProgress);
    List<JobApplication> findByJobId(Long jobId);
}