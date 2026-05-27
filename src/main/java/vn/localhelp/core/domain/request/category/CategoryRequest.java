package vn.localhelp.core.domain.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
  /** Tên danh mục công việc, bắt buộc và giới hạn độ dài để hiển thị ổn định. */
  @NotBlank(message = "Category name must not be blank")
  @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
  private String name;

  /** URL icon danh mục sau khi admin upload file lên Cloudinary. */
  @NotBlank(message = "Icon URL must not be blank")
  private String iconUrl;

  /** Mô tả ngắn về danh mục. */
  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;

  /** Mã màu đại diện của danh mục, ví dụ #f97316. */
  @NotBlank(message = "Color code must not be blank")
  private String colorCode;
}
