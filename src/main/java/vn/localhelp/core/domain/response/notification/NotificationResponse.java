package vn.localhelp.core.domain.response.notification;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import vn.localhelp.core.util.constant.NotificationType;

@Data
@Builder
public class NotificationResponse {
  private Long id;
  private String title;
  private String message;
  private NotificationType type;
  private boolean read;
  private Long targetId;
  private LocalDateTime createdAt;
}
