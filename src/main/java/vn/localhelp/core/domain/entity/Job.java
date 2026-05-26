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

/**
 * JPA Entity ánh xạ bảng jobs, lưu dữ liệu chính của công việc.
 *
 * <p>Entity này là trung tâm của các chức năng đăng công việc, cập nhật công việc và
 * hủy công việc. Creator được dùng để kiểm tra quyền sửa/hủy, jobStatus biểu diễn
 * vòng đời công việc, còn jobImages lưu các URL ảnh Android đã upload.</p>
 */
@Entity
@Getter
@Setter
@Table(name = "jobs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job {
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
  /**
   * Trạng thái hiện tại của công việc, dùng để kiểm tra điều kiện cập nhật/hủy.
   */
  @Enumerated(EnumType.STRING)
  private JobStatus jobStatus;

  @CreatedDate
  private LocalDateTime createdAt;

  /**
   * Người tạo công việc, được dùng để kiểm tra quyền cập nhật và hủy công việc.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private User creator;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "helper_id")
  private User helper;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  /**
   * Danh sách ảnh gắn với công việc.
   *
   * <p>Khi đăng công việc, service tạo JobImage từ imageUrls. Khi cập nhật ảnh,
   * orphanRemoval giúp xóa ảnh cũ không còn thuộc danh sách mới.</p>
   */
  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JobImage> jobImages;

  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  private Review review;

  /**
   * Thời điểm công việc bị hủy, được set khi creator gọi API hủy công việc.
   */
  @Column(name = "cancel_time")
  private LocalDateTime cancelTime;
}
