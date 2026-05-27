package vn.localhelp.core.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * JPA Entity ánh xạ bảng 'conversations' trong MySQL.
 *
 * <p><b>Lý do dùng String (UUID) làm khóa chính thay vì Long auto-increment:</b>
 * Conversation ID được dùng làm document ID trên Firebase Firestore.
 * UUID đảm bảo tính duy nhất toàn cầu và không thể đoán được,
 * trong khi Integer tăng dần có thể bị dự đoán và khai thác.</p>
 *
 * <p>Quan hệ: ManyToOne với User (user1, user2) – không có thứ tự cố định,
 * ConversationRepository tìm kiếm theo cả 2 chiều.</p>
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {
  @Id
  private String id;

  @ManyToOne
  @JoinColumn(name = "user1_id")
  private User user1;

  @ManyToOne
  @JoinColumn(name = "user2_id")
  private User user2;

  private LocalDateTime createdAt;
  /**
   * Tự động sinh UUID cho id và gán createdAt trước khi lưu lần đầu vào DB.
   *
   * <p>Chỉ sinh UUID nếu id == null, tránh override khi entity đã có id.</p>
   * <p>createdAt tương tự – chỉ set nếu chưa có giá trị.</p>
   */
  @PrePersist
  public void prePersist() {
    if (this.id == null) {
      this.id = java.util.UUID.randomUUID().toString();
    }

    if (this.createdAt == null) {
      this.createdAt = LocalDateTime.now();
    }
  }
}
