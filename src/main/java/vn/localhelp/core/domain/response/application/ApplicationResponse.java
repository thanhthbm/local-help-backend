package vn.localhelp.core.domain.response.application;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO trả về thông tin một helper đã ứng tuyển vào công việc.
 *
 * Dùng trong API lấy danh sách ứng tuyển để creator xem và chọn thợ phù hợp.
 */
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
