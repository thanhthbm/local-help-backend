package vn.localhelp.core.domain.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * DTO nhận dữ liệu đầu vào khi tạo đánh giá mới (POST /api/jobs/{jobId}/review).
 *
 * <p>Validation constraints:</p>
 * <ul>
 *   <li>rating: @Min(1) @Max(5) – thang điểm 1 đến 5 sao.</li>
 *   <li>comment: @NotBlank – bắt buộc nhập nhận xét, không được để trống.</li>
 * </ul>
 *
 */
@Data
public class ReviewRequest {
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;
}
