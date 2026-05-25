package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Conversation;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.ConversationMapper;
import vn.localhelp.core.domain.mapper.UserMapper;
import vn.localhelp.core.domain.response.conversation.ConversationResponse;
import vn.localhelp.core.repository.ConversationRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.error.NotFoundException;
/**
 * Service xử lý nghiệp vụ quản lý hội thoại (Conversation) trong hệ thống LocalHelp.
 *
 * <p>Chức năng chính:</p>
 * <ul>
 *   <li>Tạo hoặc tái sử dụng conversation giữa 2 người dùng.</li>
 *   <li>Lấy danh sách conversation của user hiện tại với thông tin đối phương.</li>
 * </ul>
 *
 * <p>Sử dụng SecurityContextHolder để lấy Firebase UID của user đang đăng nhập,
 * sau đó tra cứu User entity tương ứng trong MySQL.</p>
 *
 */

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final ConversationMapper conversationMapper;
  private final UserMapper userMapper;
  /**
   * Tạo mới hoặc lấy lại conversation giữa user hiện tại và targetUser.
   *
   * <p><b>Luồng xử lý:</b></p>
   * <ol>
   *   <li>Lấy Firebase UID từ SecurityContextHolder.</li>
   *   <li>Tìm User hiện tại trong DB theo UID.</li>
   *   <li>Kiểm tra targetUserId != currentUser.id (không tự nhắn tin).</li>
   *   <li>Gọi ConversationRepository.findConversationBetweenUsers() tìm 2 chiều.</li>
   *   <li>Nếu tồn tại → trả về. Nếu không → tạo Conversation mới, UUID tự sinh @PrePersist.</li>
   * </ol>
   *
   * <p><b>@Transactional:</b> Đảm bảo atomicity – nếu save thất bại, toàn bộ transaction rollback.</p>
   *
   * @param targetUserId  ID người dùng muốn nhắn tin cùng
   * @return              ConversationResponse đã được map với thông tin partner
   * @throws RuntimeException    nếu targetUserId == currentUser.id
   * @throws NotFoundException   nếu targetUser không tồn tại trong DB
   */
  @Transactional
  public ConversationResponse getOrCreateConversation(Long targetUserId){
    String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> currentUserOpt = userRepository.findByFirebaseUid(firebaseUid);
    if (currentUserOpt.isEmpty()) {
      throw new NotFoundException("User not found");
    }

    User currentUser = currentUserOpt.get();
    if (targetUserId.equals(currentUser.getId())) {
      throw new RuntimeException("You cannot text yourself");
    }

    Optional<Conversation> conversationOpt = conversationRepository.findConversationBetweenUsers(currentUser.getId(), targetUserId);

    if (conversationOpt.isPresent()) {
      return conversationMapper.toResponse(conversationOpt.get(), currentUser.getId(), userMapper);
    }

    User targetUser = userRepository.findById(targetUserId)
        .orElseThrow(() -> new NotFoundException("Target user not found"));

    Conversation conversation = Conversation.builder()
        .user1(currentUser)
        .user2(targetUser)
        .build();

    conversationRepository.save(conversation);
    return conversationMapper.toResponse(conversation, currentUser.getId(), userMapper);
  }
  /**
   * Lấy tất cả conversations của user hiện tại đang đăng nhập.
   *
   * <p>Gọi ConversationRepository.getConversationsByUserId() với điều kiện:
   * user1.id = currentUserId OR user2.id = currentUserId (tìm 2 chiều).</p>
   *
   * <p>Mỗi Conversation được map sang ConversationResponse với partner là người còn lại,
   * không phải raw user1/user2 — giúp Android hiển thị đơn giản hơn.</p>
   *
   * @return List<ConversationResponse> danh sách hội thoại, sắp xếp mặc định theo DB
   */
  public List<ConversationResponse> getMyConversations(){
    String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    List<Conversation> conversations = conversationRepository.getConversationsByUserId(currentUser.getId());

    return conversations.stream().map(
        conv -> conversationMapper.toResponse(conv, currentUser.getId(), userMapper))
        .toList();
  }

}
