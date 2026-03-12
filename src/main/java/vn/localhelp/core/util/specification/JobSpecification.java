package vn.localhelp.core.util.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.util.constant.JobStatus;

public class JobSpecification {
  public static Specification<Job> hasCreatorId(Long creatorId){
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("creator").get("id"), creatorId);
  }

  public static Specification<Job> hasJobStatus(JobStatus jobStatus){
    return (root, query, criteriaBuilder) -> {
      if (jobStatus == null) {
        return criteriaBuilder.conjunction(); // Trả về TRUE (Không lọc)
      }
      return criteriaBuilder.equal(root.get("status"), jobStatus);
    };
  }
}
