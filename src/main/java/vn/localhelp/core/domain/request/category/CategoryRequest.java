package vn.localhelp.core.domain.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
  @NotBlank(message = "Category name must not be blank")
  @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
  private String name;

  @NotBlank(message = "Icon URL must not be blank")
  private String iconUrl;

  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;
}
