package vn.localhelp.core.domain.request.auth;

import lombok.Data;

@Data
public class FirebaseLoginRequest {
  private String email;
  private String password;
  private Boolean returnSecureToken = true;
}
