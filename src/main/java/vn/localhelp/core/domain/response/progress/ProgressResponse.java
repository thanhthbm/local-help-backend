package vn.localhelp.core.domain.response.progress;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProgressResponse {
    private String stepName;
    private String description;
    private LocalDateTime time;
    private boolean isCurrent;
    private boolean isCompleted;
}