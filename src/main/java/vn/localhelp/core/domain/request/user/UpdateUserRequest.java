package vn.localhelp.core.domain.request.user;

import lombok.Data;
import vn.localhelp.core.util.constant.GenderEnum;

@Data
public class UpdateUserRequest {
  private String fullName;
  private String phone;
  private String avatarUrl;
  private GenderEnum gender;
}
