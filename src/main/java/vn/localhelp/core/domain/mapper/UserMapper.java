package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.domain.response.user.UserSummary;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse toResponse(User user);

  @Mapping(source = "completedJobs", target = "completedJobs")
  @Mapping(source = "totalReviews", target = "totalReviews")
  @Mapping(source = "averageRating", target = "averageRating")
  @Mapping(source = "responseRate", target = "responseRate")
  UserResponse toResponseWithStats(
      User user,
      int completedJobs,
      int totalReviews,
      double averageRating,
      double responseRate
  );
  UserSummary toSummary(User user);
}
