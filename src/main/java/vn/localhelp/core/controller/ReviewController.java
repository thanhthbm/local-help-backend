package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.service.ReviewService;
import vn.localhelp.core.util.annotation.ApiMessage;
/**
 * REST Controller cung cấp API xem danh sách đánh giá của người dùng.
 *
 * <p>Endpoint: GET /api/reviews/user/{userId}
 * – Trả danh sách đánh giá (reviews) mà người dùng đó đã nhận,
 *   sắp xếp theo thời gian mới nhất trước, hỗ trợ phân trang.</p>
 *
 * <p>API tạo mới review nằm trong JobController (POST /api/jobs/{jobId}/review),
 * vì review chỉ tạo được sau khi job COMPLETED.</p>
 *
 */

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    /**
     * Lấy danh sách đánh giá mà người dùng đã nhận (reviewee), có phân trang.
     *
     * <p>Pageable được tạo với Sort.by("createdAt").descending() để đánh giá
     * mới nhất hiển thị đầu tiên.</p>
     *
     * <p><b>Lưu ý tham số current:</b> API nhận current bắt đầu từ 1 (human-friendly),
     * nhưng Spring Pageable bắt đầu từ 0. Nên dùng PageRequest.of(current - 1, ...).</p>
     *
     * @param userId    ID của người dùng cần xem đánh giá
     * @param current   Số trang hiện tại (bắt đầu từ 1, default = 1)
     * @param pageSize  Số đánh giá mỗi trang (default = 5)
     * @return          ResultPaginationDTO chứa meta phân trang và danh sách ReviewResponse
     */
    @GetMapping("/user/{userId}")
    @ApiMessage("Lấy danh sách đánh giá thành công")
    public ResponseEntity<ResultPaginationDTO> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        Pageable pageable = PageRequest.of(current - 1, pageSize, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId, pageable));
    }
}
