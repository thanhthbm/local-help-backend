package vn.localhelp.core.domain.request.auth;

import lombok.Data;

@Data
public class RegisterRequest {
  private String email;
  private String password;
}
