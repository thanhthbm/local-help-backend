package vn.localhelp.core.domain.response.review;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String reviewerName;
    private String reviewerAvatar;
    private LocalDateTime createdAt;
}