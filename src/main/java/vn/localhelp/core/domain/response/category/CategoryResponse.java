package vn.localhelp.core.domain.response.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
  /** Id danh mục để app/admin dùng khi chọn, sửa hoặc xóa. */
  private Long id;
  /** Tên danh mục hiển thị cho người dùng. */
  private String name;
  /** Icon đại diện danh mục. */
  private String iconUrl;
  /** Màu đại diện dùng ở UI và biểu đồ. */
  private String colorCode;
}
