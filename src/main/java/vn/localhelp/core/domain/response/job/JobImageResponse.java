package vn.localhelp.core.domain.response.job;

import lombok.Builder;
import lombok.Data;

/**
 * DTO trả về thông tin một ảnh gắn với công việc.
 *
 * Trong luồng hoàn thành việc, DTO này chủ yếu đại diện cho ảnh bằng chứng loại PROOF.
 */
@Data
@Builder
public class JobImageResponse {
    private Long id;
    private String imageUrl;
    private String imageType;
}
