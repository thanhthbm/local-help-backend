package vn.localhelp.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.localhelp.core.domain.request.user.UpdateProfileRequest;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.service.UserService;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.annotation.ApiMessage;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.util.constant.UserStatus;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  @ApiMessage("Lấy thông tin hồ sơ thành công")
  public ResponseEntity<UserResponse> getProfile() {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(userService.getMyProfile(currentUid));
  }

  @GetMapping("/{id}")
  @ApiMessage("Lấy thông tin người dùng thành công")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getUserById(id));
  }

  @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiMessage("Cập nhật hồ sơ thành công")
  public ResponseEntity<UserResponse> updateProfile(
      @RequestPart(value = "data", required = false) @Valid UpdateProfileRequest request,
      @RequestPart(value = "avatar", required = false) MultipartFile avatarFile
  ) {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(userService.updateProfile(currentUid, request, avatarFile));
  }

  @GetMapping("/count-users")
  @ApiMessage("Lấy số lượng người dùng thành công")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Long> countUsers() {
    long count = userService.countTotalUsers();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/admin/all")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiMessage("Lấy danh sách người dùng cho Admin thành công")
  public ResponseEntity<ResultPaginationDTO<List<UserResponse>>> getAllUsers(
          @RequestParam(defaultValue = "1") int current,
          @RequestParam(defaultValue = "10") int pageSize
  ) {
    return ResponseEntity.ok(userService.getAllUsersForAdmin(current, pageSize));
  }

  @PutMapping("/admin/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiMessage("Cập nhật trạng thái người dùng thành công")
  public ResponseEntity<UserResponse> updateUserStatus(
          @PathVariable Long id,
          @RequestParam UserStatus status
  ) {
    return ResponseEntity.ok(userService.updateUserStatus(id, status));
  }
}
