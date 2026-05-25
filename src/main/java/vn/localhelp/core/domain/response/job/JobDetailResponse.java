package vn.localhelp.core.domain.response.job;

import lombok.Builder;
import lombok.Data;
import vn.localhelp.core.domain.response.progress.ProgressResponse;

import java.util.List;
/**
 * DTO trả về thông tin chi tiết một công việc, bao gồm tiến độ thực hiện.
 *
 * <p>Dùng trong màn hình chi tiết công việc, cho phép người dùng
 * xem timeline tiến độ (APPLIED → ACCEPTED → IN_PROGRESS → COMPLETED).</p>
 *
 * <p>@Builder cho phép tạo linh hoạt trong JobService, chỉ set các trường cần thiết.</p>
 *
 */
@Data
@Builder
public class JobDetailResponse {
    private JobResponse jobInfo;
    private String description;
    private List<ProgressResponse> progresses;
}
