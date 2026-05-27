package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.domain.response.user.UserSummary;

@Mapper(componentModel = "spring")
public interface UserMapper {
  /**
   * Map entity User sang response cơ bản, không bổ sung thống kê hồ sơ.
   */
  UserResponse toResponse(User user);

  /**
   * Map entity User sang response có thống kê hiển thị trên màn hình hồ sơ.
   */
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

  /**
   * Map User sang dữ liệu tóm tắt để nhúng trong các response khác.
   */
  UserSummary toSummary(User user);
}
