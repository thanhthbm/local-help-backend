package vn.localhelp.core.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.response.auth.LoginResponse;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.service.AuthService;
import vn.localhelp.core.util.annotation.ApiMessage;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @ApiMessage("Login success")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(HttpServletRequest request) {
    return ResponseEntity.ok(authService.loginWithFirebase(request));
  }

  @ApiMessage("Register success")
  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(HttpServletRequest request) {
    return ResponseEntity.ok(authService.registerWithFirebase(request));
  }
}
