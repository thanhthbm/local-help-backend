package vn.localhelp.core.domain.response.application;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long applicationId;
    private Long helperId;
    private String helperName;
    private String helperAvatar;
    private Double helperRating;
    private String status;
    private LocalDateTime appliedAt;
}
