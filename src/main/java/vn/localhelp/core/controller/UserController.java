package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.service.UserService;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.annotation.ApiMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.util.constant.UserStatus;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getProfile(){
    String currentUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(userService.getMyProfile(currentUid));
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
  ){
    return ResponseEntity.ok(userService.getAllUsersForAdmin(current, pageSize));
  }

  @PutMapping("/admin/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiMessage("Cập nhật trạng thái người dùng thành công")
  public ResponseEntity<UserResponse> updateUserStatus(
          @PathVariable Long id,
          @RequestParam UserStatus status
  ){
    return ResponseEntity.ok(userService.updateUserStatus(id, status));
  }
}
