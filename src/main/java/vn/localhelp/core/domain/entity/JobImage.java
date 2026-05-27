package vn.localhelp.core.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.localhelp.core.util.constant.ImageType;

/**
 * JPA Entity ánh xạ bảng job_images, lưu URL ảnh gắn với công việc.
 *
 * <p>Ảnh loại REQUEST được dùng trong chức năng đăng/cập nhật công việc. Ảnh loại PROOF
 * được dùng ở luồng minh chứng hoàn thành công việc.</p>
 */
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_images")
public class JobImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  /**
   * Phân loại ảnh: REQUEST là ảnh mô tả công việc, PROOF là ảnh bằng chứng hoàn thành.
   */
  @Enumerated(EnumType.STRING)
  private ImageType imageType;

  /**
   * Công việc sở hữu ảnh này.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

}
