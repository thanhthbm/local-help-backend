package vn.localhelp.core.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Notifications;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.NotificationMapper;
import vn.localhelp.core.domain.response.notification.NotificationResponse;
import vn.localhelp.core.repository.NotificationRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final NotificationMapper notificationMapper;

  public List<NotificationResponse> getNotificationsForUser(String firebaseUid) {
    User user = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
        .stream()
        .map(notificationMapper::toResponse)
        .toList();
  }

  public NotificationResponse markAsRead(Long notificationId, String firebaseUid) {
    Notifications notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));

    User user = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (!notification.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("Access denied: notification does not belong to current user");
    }

    notification.setRead(true);
    return notificationMapper.toResponse(notificationRepository.save(notification));
  }
}
