package vn.localhelp.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.mapper.ReviewMapper;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.domain.response.review.ReviewResponse;
import vn.localhelp.core.repository.ReviewRepository;
/**
 * Service xử lý nghiệp vụ liên quan đến đánh giá (Review) trong LocalHelp.
 *
 * <p>Chức năng chính: Truy vấn và phân trang danh sách reviews của một người dùng.
 * Logic tạo review mới nằm trong JobService.createReview() vì gắn với vòng đời Job.</p>
 *
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    /**
     * Lấy danh sách đánh giá phân trang của người dùng (theo reviewee_id).
     *
     * <p>Tạo ResultPaginationDTO.Meta với thông tin phân trang:
     * page = pageable.getPageNumber() + 1 (chuyển từ 0-indexed sang 1-indexed),
     * pages = reviewPage.getTotalPages(),
     * total = reviewPage.getTotalElements().</p>
     *
     * @param userId   ID người dùng cần lấy reviews (là reviewee)
     * @param pageable Đối tượng phân trang và sắp xếp (tạo từ Controller)
     * @return         ResultPaginationDTO chứa meta + List<ReviewResponse>
     */
    public ResultPaginationDTO getReviewsForUser(Long userId, Pageable pageable) {
        Page<vn.localhelp.core.domain.entity.Review> reviewPage = reviewRepository.findByRevieweeId(userId, pageable);
        
        ResultPaginationDTO.Meta meta = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .pages(reviewPage.getTotalPages())
                .total(reviewPage.getTotalElements())
                .build();

        return ResultPaginationDTO.builder()
                .meta(meta)
                .result(reviewPage.getContent().stream()
                        .map(reviewMapper::toResponse)
                        .toList())
                .build();
    }
}
