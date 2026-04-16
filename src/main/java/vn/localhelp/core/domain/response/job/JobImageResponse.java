package vn.localhelp.core.domain.response.job;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobImageResponse {
    private Long id;
    private String imageUrl;
    private String imageType;
}
