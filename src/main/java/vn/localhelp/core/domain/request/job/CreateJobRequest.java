package vn.localhelp.core.domain.request.job;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
// DTO nhận dữ liệu từ Android cho API đăng công việc mới và cập nhật công việc.
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
