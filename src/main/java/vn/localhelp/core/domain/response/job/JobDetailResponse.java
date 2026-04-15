package vn.localhelp.core.domain.response.job;

import lombok.Builder;
import lombok.Data;
import vn.localhelp.core.domain.response.progress.ProgressResponse;

import java.util.List;

@Data
@Builder
public class JobDetailResponse {
    private JobResponse jobInfo;
    private String description;
    private List<ProgressResponse> progresses;
}
