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
  private Long id;
  private String name;
  private String iconUrl;
  private String colorCode;
}
