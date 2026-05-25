package vn.localhelp.core.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.entity.Conversation;
import vn.localhelp.core.domain.response.conversation.ConversationResponse;
import vn.localhelp.core.service.ConversationService;
import vn.localhelp.core.util.annotation.ApiMessage;
/**
 * REST Controller xử lý các yêu cầu liên quan đến cuộc hội thoại (Conversation).
 *
 * <p>Cung cấp 2 endpoint chính:</p>
 * <ul>
 *   <li>POST /api/conversations/start  – Tạo hoặc lấy lại hội thoại với người dùng khác (idempotent).</li>
 *   <li>GET  /api/conversations         – Lấy danh sách tất cả hội thoại của user hiện tại.</li>
 * </ul>
 *
 * <p>Tất cả endpoint yêu cầu xác thực Firebase JWT (qua FirebaseAuthFilter).</p>
 *
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations")
public class ConversationController {
  private final ConversationService conversationService;
  /**
   * Tạo mới hoặc lấy lại hội thoại giữa user hiện tại và user mục tiêu.
   *
   * <p><b>Idempotent:</b> Nếu đã tồn tại conversation giữa 2 người, trả về conversation cũ.
   * Gọi nhiều lần với cùng targetUserId không tạo ra bản ghi trùng lặp.</p>
   *
   * <p>Conversation ID được sinh dưới dạng UUID và dùng làm document ID trên Firebase Firestore,
   * cho phép Android SDK truy cập đúng collection messages theo realtime.</p>
   *
   * @param targetUserId  ID (Long) của người dùng muốn bắt đầu nhắn tin
   * @return              ConversationResponse chứa: id (UUID), partner (UserSummary), createdAt
   * @throws RuntimeException nếu user cố tình tự nhắn tin cho chính mình
   */

  @ApiMessage("Start a conversation with a user successfully")
  @PostMapping("/start")
  public ResponseEntity<ConversationResponse> startConversation(@RequestParam Long targetUserId) {
    return ResponseEntity.ok(this.conversationService.getOrCreateConversation(targetUserId));
  }
  /**
   * Lấy toàn bộ danh sách hội thoại của user hiện tại, kèm thông tin đối phương (partner).
   *
   * <p>Mỗi ConversationResponse trả về thông tin partner thay vì user1/user2 raw,
   * giúp phía Android hiển thị trực tiếp mà không cần xử lý thêm.</p>
   *
   * @return List<ConversationResponse> danh sách hội thoại, có thể rỗng nếu chưa có
   */
  @ApiMessage("Fetch all my conversations successfully")
  @GetMapping
  public ResponseEntity<List<ConversationResponse>> getAllConversations() {
    return ResponseEntity.ok(this.conversationService.getMyConversations());
  }
}
