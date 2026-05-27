package vn.localhelp.core.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
/**
 * JPA Entity ánh xạ bảng 'reviews' trong MySQL.
 *
 * <p>Ràng buộc nghiệp vụ quan trọng:</p>
 * <ul>
 *   <li>job_id có UNIQUE constraint → mỗi job chỉ được đánh giá đúng 1 lần.</li>
 *   <li>job_id nullable=false → mọi review đều phải gắn với 1 job.</li>
 *   <li>@CreatedDate tự động gán createdAt khi entity được persist lần đầu.</li>
 *   <li>reviewer LAZY loaded → tránh N+1 khi query danh sách reviews.</li>
 * </ul>
 *
 * <p>Khi reviewer xóa tài khoản, reviewer_id FK có thể thành null (SET NULL)
 * nên ReviewMapper xử lý null-safe.</p>
 *
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reviews")
public class Review {
  /** Khóa chính của đánh giá. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /** Điểm đánh giá dùng để tính averageRating trên hồ sơ. */
  private Integer rating;
  @Column(columnDefinition = "TEXT")
  private String comment;

  @CreatedDate
  private LocalDateTime createdAt;

  /** Job được đánh giá, mỗi job chỉ có một review. */
  @OneToOne
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  /** Người viết đánh giá. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewer_id")
  private User reviewer;

  /** Người nhận đánh giá; hồ sơ dùng trường này để đếm và tính điểm trung bình. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewee_id")
  private User reviewee;
}
