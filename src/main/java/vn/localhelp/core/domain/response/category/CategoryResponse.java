package vn.localhelp.core.domain.response.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
  private Long id;
  private String name;
  private String iconUrl;
}
