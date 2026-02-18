package vn.localhelp.core.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseService {
  public void setUserRole(String uid, String role) throws FirebaseAuthException {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);

    FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
  }
}
