package vn.localhelp.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.util.constant.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {

  List<Job> findByJobStatus(JobStatus jobStatus);

  List<Job> findByCategoryId(Long categoryId);

  List<Job> findByCreatorId(Long creatorId);

  List<Job> findByHelperId(Long helperId);
}
