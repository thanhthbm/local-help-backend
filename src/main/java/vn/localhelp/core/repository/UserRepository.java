package vn.localhelp.core.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.util.constant.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByFirebaseUid(String localId);
  /** Đếm số user theo role, dùng cho trang thống kê/admin. */
  long countByRole(UserRole role);
}
