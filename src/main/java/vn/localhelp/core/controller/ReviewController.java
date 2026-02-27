package vn.localhelp.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.request.review.CreateReviewRequest;
import vn.localhelp.core.domain.response.review.ReviewResponse;
import vn.localhelp.core.service.ReviewService;
import vn.localhelp.core.util.FirebaseUtil;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ReviewResponse> createReview(
      @Valid @RequestBody CreateReviewRequest request) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(reviewService.createReview(currentFirebaseUid, request));
  }

  @GetMapping("/job/{jobId}")
  public ResponseEntity<ReviewResponse> getReviewByJobId(@PathVariable Long jobId) {
    return ResponseEntity.ok(reviewService.getReviewByJobId(jobId));
  }
}
