package vn.localhelp.core.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.util.constant.JobStatus;

public class JobSpecification {

  public static Specification<Job> hasJobStatus(JobStatus jobStatus) {
    return (root, query, criteriaBuilder) -> {
      if (jobStatus == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("jobStatus"), jobStatus);
    };
  }

  public static Specification<Job> notCreatedByFirebaseUid(String firebaseUid) {
    return (root, query, criteriaBuilder) -> {
      if (firebaseUid == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.notEqual(root.get("creator").get("firebaseUid"), firebaseUid);
    };
  }

  public static Specification<Job> hasKeyword(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(keyword)) {
        return criteriaBuilder.conjunction();
      }
      String likePattern = "%" + keyword.toLowerCase() + "%";
      Predicate titleLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern);
      Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
      return criteriaBuilder.or(titleLike, descriptionLike);
    };
  }

  public static Specification<Job> hasCategory(Long categoryId) {
    return (root, query, criteriaBuilder) -> {
      if (categoryId == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    };
  }

    public static Specification<Job> hasCreatorId(Long creatorId) {
        return (root, query, criteriaBuilder) -> {
            if (creatorId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("creator").get("id"), creatorId);
        };
    }
}
