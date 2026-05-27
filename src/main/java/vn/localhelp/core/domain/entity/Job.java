package vn.localhelp.core.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.localhelp.core.util.constant.JobStatus;

@Entity
@Getter
@Setter
@Table(name = "jobs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job {
  /** Khóa chính của công việc, cũng được dùng như id giao dịch trong màn thống kê. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  private Double price;
  private String address;
  private Double latitude;
  private Double longitude;
  @Enumerated(EnumType.STRING)
  private JobStatus jobStatus;

  /** Thời điểm tạo job; FinanceService dùng để lọc theo tháng và chia biểu đồ tuần. */
  @CreatedDate
  private LocalDateTime createdAt;

  /** Người tạo job, được tính là spending khi job hoàn thành. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private User creator;
  /** Người nhận làm job, được tính là earning khi job hoàn thành. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "helper_id")
  private User helper;
  /** Danh mục dùng để gom nhóm thống kê và hiển thị icon/màu trên app. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobImage> jobImages;

  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  private Review review;

  @Column(name = "cancel_time")
  private LocalDateTime cancelTime;
}
