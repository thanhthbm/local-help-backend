package vn.localhelp.core.domain.response.auth;

import lombok.Data;

@Data
public class FirebaseLoginResponse {
  private String idToken;
  private String refreshToken;
  private String expiresIn;
  private String localId;
  private String email;
}
