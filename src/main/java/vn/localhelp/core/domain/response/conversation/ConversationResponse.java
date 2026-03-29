package vn.localhelp.core.domain.response.conversation;

import java.time.LocalDateTime;
import lombok.Data;
import vn.localhelp.core.domain.response.user.UserSummary;

@Data
public class ConversationResponse {
  private String id;
  private UserSummary partner;
  private LocalDateTime createdAt;
}
