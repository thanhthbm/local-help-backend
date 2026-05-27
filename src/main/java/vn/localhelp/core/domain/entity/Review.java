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
