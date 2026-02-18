package vn.localhelp.core.domain.request.job;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateJobRequest {
  private String title;
  private String description;
  private Double price;
  private String address;
  private Double latitude;
  private Double longitude;
  private Long categoryId;
  private List<String> imageUrls;
}
