package vn.localhelp.core.domain.mapper;

import org.springframework.stereotype.Component;
import vn.localhelp.core.domain.entity.Review;
import vn.localhelp.core.domain.response.review.ReviewResponse;

@Component
public class ReviewMapper {
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
