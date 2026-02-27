package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.request.user.UpdateUserRequest;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.service.UserService;
import vn.localhelp.core.util.FirebaseUtil;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getProfile() {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(userService.getMyProfile(currentUid));
  }

  @PatchMapping("/me")
  public ResponseEntity<UserResponse> updateProfile(@RequestBody UpdateUserRequest request) {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(userService.updateProfile(currentUid, request));
  }
}
