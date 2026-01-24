package vn.localhelp.core.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.User;
import vn.localhelp.core.domain.response.auth.LoginResponse;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;

  @Value("${firebase.api.key}")
  private String firebaseApiKey;

  public LoginResponse loginWithFirebase(HttpServletRequest request) {
    String uid = (String) request.getAttribute("firebaseUid");
    String email = (String) request.getAttribute("email");


    User user = userRepository.findByFirebaseUid(uid)
        .orElseThrow(() -> new RuntimeException("User not registered"));


    return LoginResponse.builder()
        .tokenType("Bearer")
        .user(UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole())
            .build())
        .build();
  }


  public UserResponse registerWithFirebase(HttpServletRequest request) {
    String uid = (String) request.getAttribute("firebaseUid");
    String email = (String) request.getAttribute("email");


    return userRepository.findByFirebaseUid(uid)
        .map(u -> UserResponse.builder()
            .id(u.getId())
            .email(u.getEmail())
            .role(u.getRole())
            .build())
        .orElseGet(() -> {
          User user = userRepository.save(
              User.builder()
                  .firebaseUid(uid)
                  .email(email)
                  .role("USER")
                  .createdAt(LocalDateTime.now())
                  .build()
          );
          return UserResponse.builder()
              .id(user.getId())
              .email(user.getEmail())
              .role(user.getRole())
              .build();
        });
  }

}
