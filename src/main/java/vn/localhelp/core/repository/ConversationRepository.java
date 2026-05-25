package vn.localhelp.core.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.Conversation;
/**
 * Spring Data JPA Repository cho entity Conversation.
 *
 * <p>Kế thừa JpaRepository<Conversation, String> vì Conversation.id kiểu String (UUID),
 * không phải Long – để đồng bộ với document ID trên Firebase Firestore.</p>
 *
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
  /**
   * Tìm conversation giữa hai người dùng theo cả 2 chiều.
   *
   * <p>JPQL query kiểm tra cả (user1=A, user2=B) lẫn (user1=B, user2=A) vì khi tạo
   * conversation, hệ thống không cố định ai là user1, ai là user2.</p>
   *
   * <p>Trả về Optional.empty() nếu chưa có conversation giữa 2 người.</p>
   *
   * @param userId1  ID người thứ nhất
   * @param userId2  ID người thứ hai
   * @return         Optional<Conversation> – có thể rỗng nếu chưa tồn tại
   */
  @Query("SELECT c FROM Conversation c WHERE " +
      "(c.user1.id = :userId1 AND c.user2.id = :userId2) OR " +
      "(c.user1.id = :userId2 AND c.user2.id = :userId1)")
  Optional<Conversation> findConversationBetweenUsers(
      @Param("userId1") Long userId1,
      @Param("userId2") Long userId2
  );
  /**
   * Lấy tất cả conversations có chứa user với ID cho trước (là user1 hoặc user2).
   *
   * <p>Dùng JPQL tìm 2 chiều: c.user1.id = :currentUserId OR c.user2.id = :currentUserId</p>
   *
   * @param currentUserId  ID của user cần lấy danh sách hội thoại
   * @return               List<Conversation> toàn bộ conversations của user
   */
  @Query(
      "SELECT c from Conversation c WHERE"
      + "(c.user1.id = :currentUserId OR c.user2.id = :currentUserId)"
  )
  List<Conversation> getConversationsByUserId(Long currentUserId);
}