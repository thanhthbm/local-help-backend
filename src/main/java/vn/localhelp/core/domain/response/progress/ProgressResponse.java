package vn.localhelp.core.domain.response.progress;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO trả về một bước trong timeline tiến trình công việc.
 *
 * stepName là tên trạng thái nghiệp vụ, description là mô tả hiển thị,
 * time là thời điểm xảy ra, còn isCurrent/isCompleted giúp client vẽ đúng bước hiện tại.
 */
@Data
@Builder
public class ProgressResponse {
    private String stepName;
    private String description;
    private LocalDateTime time;
    private boolean isCurrent;
    private boolean isCompleted;
}
