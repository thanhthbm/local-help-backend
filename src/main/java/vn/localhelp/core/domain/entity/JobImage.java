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

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_images")
// Entity lưu ảnh gắn với công việc, gồm ảnh yêu cầu khi đăng/sửa và ảnh minh chứng khi hoàn thành.
public class JobImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  // Phân loại ảnh: REQUEST cho ảnh mô tả công việc, PROOF cho ảnh bằng chứng hoàn thành.
  @Enumerated(EnumType.STRING)
  private ImageType imageType;

  // Nhiều ảnh có thể thuộc cùng một công việc.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id")
  private Job job;

}
