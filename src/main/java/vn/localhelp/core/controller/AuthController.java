package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.request.auth.LoginRequest;
import vn.localhelp.core.domain.request.auth.RegisterRequest;
import vn.localhelp.core.domain.response.auth.LoginResponse;
import vn.localhelp.core.service.AuthService;
import vn.localhelp.core.util.annotation.ApiMessage;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @ApiMessage("Login success")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @ApiMessage("Register success")
  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok().build();
  }
}
