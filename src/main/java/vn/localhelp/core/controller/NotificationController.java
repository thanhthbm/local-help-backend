package vn.localhelp.core.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.response.notification.NotificationResponse;
import vn.localhelp.core.service.NotificationService;
import vn.localhelp.core.util.FirebaseUtil;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(notificationService.getNotificationsForUser(currentFirebaseUid));
  }

  @PatchMapping("/{id}/read")
  public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(notificationService.markAsRead(id, currentFirebaseUid));
  }
}
