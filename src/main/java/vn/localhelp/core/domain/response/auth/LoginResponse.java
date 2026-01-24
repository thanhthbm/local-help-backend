package vn.localhelp.core.domain.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.localhelp.core.domain.response.user.UserResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
  private String accessToken;
  private String tokenType;
  private Long expiresIn;
  private UserResponse user;
}
