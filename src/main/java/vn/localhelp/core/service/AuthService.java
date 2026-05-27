package vn.localhelp.core.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.UserMapper;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.constant.UserRole;
import vn.localhelp.core.util.constant.UserStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  /**
   * Tạo hoặc cập nhật user nội bộ từ Firebase user.
   *
   * Đăng ký tài khoản diễn ra ở Firebase phía client. Khi người dùng đăng nhập,
   * backend lấy thông tin Firebase bằng uid, rồi upsert bảng users để các module
   * hồ sơ, công việc, thống kê có userId nội bộ để liên kết dữ liệu.
   */
  @Transactional
  public UserResponse syncUserFromFirebase(String uid){
    UserRecord firebaseUser;
    log.info("firebase user id is {}", uid);

    try {
      firebaseUser = FirebaseAuth.getInstance().getUser(uid);
    } catch (FirebaseAuthException e) {
      throw new RuntimeException("Lỗi kết nối Firebase", e);
    }

    User user = userRepository.findByFirebaseUid(uid)
        .orElseGet(() -> User.builder()
            .firebaseUid(uid)
            .reputationScore(5.0)
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .build());

    if (user.getStatus() != UserStatus.ACTIVE){
      throw new RuntimeException("User is not active");
    }

    user.setEmail(firebaseUser.getEmail());
    user.setPhone(firebaseUser.getPhoneNumber());
    log.debug(user.toString());

    User savedUser = userRepository.save(user);
    return userMapper.toResponse(savedUser);
  }

}
