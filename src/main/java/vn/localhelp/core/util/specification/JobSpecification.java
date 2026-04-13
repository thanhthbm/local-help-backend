package vn.localhelp.core.util.specification;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
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
      return criteriaBuilder.equal(root.get("jobStatus"), jobStatus);
    };
  }

  public static Specification<Job> hasKeyword(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(keyword)) {
        return criteriaBuilder.conjunction();
      }

      String pattern = "%" + keyword.toLowerCase() + "%";

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), pattern),
          criteriaBuilder.like(criteriaBuilder.lower(root.join("category", JoinType.LEFT).get("name")), pattern)
      );
    };
  }

  public static Specification<Job> notCreatedByFirebaseUid(String firebaseUid) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(firebaseUid)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.notEqual(root.get("creator").get("firebaseUid"), firebaseUid);
    };
  }
}
