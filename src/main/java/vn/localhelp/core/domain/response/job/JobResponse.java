package vn.localhelp.core.domain.response.job;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import vn.localhelp.core.util.constant.JobStatus;
/**
 * DTO trả về thông tin công việc cho client Android.
 *
 * <p>Các trường nullable khi job ở trạng thái OPEN (chưa có helper):</p>
 * <ul>
 *   <li>helperId, helperName, helperAvatar, helperRating – null khi job OPEN.</li>
 * </ul>
 *
 * <p>Trường distance (km) chỉ có giá trị khi client truyền lat/lng trong request.
 * Null nếu không có tọa độ người dùng.</p>
 *
 */
@Data
@Builder
public class JobResponse {
  private Long id;
  private String title;
  private String description;
  private Double price;
  private String address;
  private Double latitude;
  private Double longitude;
  private JobStatus status;
  private String bio;
  private String categoryName;
  private String categoryIcon;
  private String creatorName;
  private Long creatorId;
  private String creatorAvatar;
  private Double creatorRating;
  private List<String> images;
  private LocalDateTime createdAt;
  private Double distance;

  private Long helperId;
  private String helperName;
  private String helperAvatar;
  private Double helperRating;
}
