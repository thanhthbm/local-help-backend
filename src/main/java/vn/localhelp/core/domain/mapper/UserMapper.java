package vn.localhelp.core.domain.mapper;

import org.mapstruct.Mapper;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.domain.response.user.UserSummary;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserResponse toResponse(User user);
  UserSummary toSummary(User user);
}
