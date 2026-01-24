package vn.localhelp.core.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.User;
import vn.localhelp.core.domain.request.auth.FirebaseLoginRequest;
import vn.localhelp.core.domain.request.auth.LoginRequest;
import vn.localhelp.core.domain.request.auth.RegisterRequest;
import vn.localhelp.core.domain.response.auth.FirebaseLoginResponse;
import vn.localhelp.core.domain.response.auth.LoginResponse;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.client.FirebaseAuthFeignClient;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final FirebaseAuthFeignClient firebaseAuthFeignClient;

  @Value("${firebase.api.key}")
  private String firebaseApiKey;

  public void register(RegisterRequest registerRequest) {
    try {
      UserRecord.CreateRequest firebaseRequest = new CreateRequest()
          .setEmail(registerRequest.getEmail())
          .setPassword(registerRequest.getPassword());

      UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(firebaseRequest);

      User user = User.builder()
          .firebaseUid(firebaseUser.getUid())
          .email(registerRequest.getEmail())
          .role("USER")
          .createdAt(LocalDateTime.now())
          .build();

      userRepository.save(user);
    }catch (FirebaseAuthException e){
      throw new RuntimeException("Register failed: " + e.getMessage());
    }
  }

  public LoginResponse login(LoginRequest loginRequest) {
    FirebaseLoginRequest firebaseLoginRequest = new FirebaseLoginRequest();
    firebaseLoginRequest.setEmail(loginRequest.getEmail());
    firebaseLoginRequest.setPassword(loginRequest.getPassword());

    FirebaseLoginResponse firebaseLoginResponse = firebaseAuthFeignClient.login(firebaseApiKey, firebaseLoginRequest);

    User user = userRepository.findByFirebaseUid(firebaseLoginResponse.getLocalId())
        .orElseGet(() -> userRepository.save(
            User.builder()
                .firebaseUid(firebaseLoginResponse.getLocalId())
                .email(firebaseLoginResponse.getEmail())
                .role("USER")
                .build()
        ));

    return LoginResponse.builder()
        .accessToken(firebaseLoginResponse.getIdToken())
        .tokenType("Bearer")
        .expiresIn(Long.parseLong(firebaseLoginResponse.getExpiresIn()))
        .user(
            UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build()
        )
        .build();
  }
}
