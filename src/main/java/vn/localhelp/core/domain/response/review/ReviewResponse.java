package vn.localhelp.core.domain.response.review;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * DTO trả về thông tin một đánh giá cho client Android.
 *
 * <p>Trường reviewerAvatar có thể null nếu reviewer chưa cập nhật ảnh đại diện
 * hoặc tài khoản reviewer đã bị xóa.</p>
 *
 * <p>@Builder cho phép tạo đối tượng linh hoạt trong ReviewMapper.toResponse().</p>
 *
 */
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