package vn.localhelp.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.localhelp.core.domain.entity.Notifications;

public interface NotificationRepository extends JpaRepository<Notifications, Long> {

  List<Notifications> findByUserIdOrderByCreatedAtDesc(Long userId);

  long countByUserIdAndReadFalse(Long userId);
}
