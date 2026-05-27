package vn.localhelp.core.domain.request.job;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * DTO nhận dữ liệu từ Android cho API đăng mới và cập nhật công việc.
 *
 * <p>Được dùng chung cho POST /api/jobs và PUT /api/jobs/{id}. Khi cập nhật, các field null
 * được service hiểu là không thay đổi giá trị cũ.</p>
 */
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
