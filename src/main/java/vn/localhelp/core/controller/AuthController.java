package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.service.AuthService;
import vn.localhelp.core.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @ApiMessage("Login sync user from Firebase")
  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(){
    String uid = SecurityContextHolder.getContext().getAuthentication().getName();

    UserResponse user = authService.syncUserFromFirebase(uid);
    return ResponseEntity.ok(user);
  }
}
