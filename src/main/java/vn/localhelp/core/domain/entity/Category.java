package vn.localhelp.core.domain.entity;

import com.google.type.DateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity ánh xạ bảng categories, lưu danh mục công việc.
 *
 * <p>Khi đăng hoặc cập nhật công việc, backend tìm Category theo categoryId trong
 * CreateJobRequest và gán vào Job trước khi lưu database.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String iconUrl;
  @Column(columnDefinition = "TEXT")
  private String description;
  private String colorCode;
}
