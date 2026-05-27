package vn.localhelp.core.domain.response.conversation;

import java.time.LocalDateTime;
import lombok.Data;
import vn.localhelp.core.domain.response.user.UserSummary;
/**
 * DTO trả về thông tin một cuộc hội thoại cho client Android.
 *
 * <p>Thay vì trả raw user1 và user2, chỉ trả 'partner' – là người dùng còn lại
 * (không phải user hiện tại). Logic xác định partner nằm trong ConversationMapper.</p>
 *
 * <p>Trường id (String/UUID) được Android dùng làm documentPath trên Firestore:
 * conversations/{id}/messages để lắng nghe tin nhắn realtime.</p>
 *
 */
@Data
public class ConversationResponse {
  private String id;
  private UserSummary partner;
  private LocalDateTime createdAt;
}
