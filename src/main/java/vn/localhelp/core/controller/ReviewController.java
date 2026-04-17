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

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

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
