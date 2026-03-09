package vn.localhelp.core.domain.response.review;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {
  private Long id;
  private Integer rating;
  private String comment;
  private Long jobId;
  private Long reviewerId;
  private String reviewerName;
  private String reviewerAvatar;
  private Long revieweeId;
  private String revieweeName;
  private LocalDateTime createdAt;
}
