package vn.localhelp.core.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.util.constant.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Tìm user backend theo Firebase UID.
   *
   * <p>Được dùng sau khi FirebaseAuthFilter xác thực token để lấy User entity hiện tại
   * trong các chức năng đăng, cập nhật và hủy công việc.</p>
   *
   * @param localId Firebase UID của người dùng
   * @return        Optional<User>, rỗng nếu UID chưa có trong database
   */
  Optional<User> findByFirebaseUid(String localId);
  long countByRole(UserRole role);
}
