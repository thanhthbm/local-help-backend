package vn.localhelp.core.domain.mapper;

import org.springframework.stereotype.Component;
import vn.localhelp.core.domain.entity.Review;
import vn.localhelp.core.domain.response.review.ReviewResponse;
/**
 * Mapper thủ công chuyển đổi Review entity sang ReviewResponse DTO.
 *
 * <p>Không dùng MapStruct vì cần null-safe handling đặc biệt:
 * reviewer có thể là null nếu tài khoản người đánh giá đã bị xóa.</p>
 *
 */
@Component
public class ReviewMapper {
    /**
     * Chuyển đổi Review entity sang ReviewResponse DTO.
     *
     * <p><b>Null-safe handling cho reviewer:</b>
     * Nếu reviewer == null (tài khoản đã bị xóa), dùng tên mặc định
     * 'Người dùng Local Help' và avatar = null.</p>
     *
     * @param review  Review entity cần chuyển đổi
     * @return        ReviewResponse DTO, null nếu review đầu vào là null
     */
    public ReviewResponse toResponse(Review review) {
        if (review == null) return null;
        
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewerName(review.getReviewer() != null ? review.getReviewer().getFullName() : "Người dùng Local Help")
                .reviewerAvatar(review.getReviewer() != null ? review.getReviewer().getAvatarUrl() : null)
                .createdAt(review.getCreatedAt())
                .build();
    }
}
