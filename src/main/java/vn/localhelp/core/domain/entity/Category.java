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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category {
  /** Id danh mục, khóa chính trong bảng categories. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /** Tên danh mục hiển thị ở app và admin. */
  private String name;
  /** URL icon danh mục, thường được upload lên Cloudinary từ admin. */
  private String iconUrl;
  /** Mô tả chi tiết danh mục, dùng cho quản trị/nội dung mở rộng. */
  @Column(columnDefinition = "TEXT")
  private String description;
  /** Mã màu dùng để render biểu đồ thống kê và chip danh mục. */
  private String colorCode;
}
