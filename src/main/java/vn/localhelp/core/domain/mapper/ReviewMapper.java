package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.localhelp.core.domain.entity.Review;
import vn.localhelp.core.domain.response.review.ReviewResponse;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

  @Mapping(source = "job.id", target = "jobId")
  @Mapping(source = "reviewer.id", target = "reviewerId")
  @Mapping(source = "reviewer.fullName", target = "reviewerName")
  @Mapping(source = "reviewer.avatarUrl", target = "reviewerAvatar")
  @Mapping(source = "reviewee.id", target = "revieweeId")
  @Mapping(source = "reviewee.fullName", target = "revieweeName")
  ReviewResponse toResponse(Review review);
}
