package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.Review;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.ReviewMapper;
import vn.localhelp.core.domain.request.review.CreateReviewRequest;
import vn.localhelp.core.domain.response.review.ReviewResponse;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.ReviewRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.NotFoundException;
import vn.localhelp.core.util.constant.JobStatus;

@Service
@RequiredArgsConstructor
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final ReviewMapper reviewMapper;

  @Transactional
  public ReviewResponse createReview(String reviewerFirebaseUid, CreateReviewRequest request) {
    Job job = jobRepository.findById(request.getJobId())
        .orElseThrow(() -> new NotFoundException("Job not found with id: " + request.getJobId()));

    if (job.getJobStatus() != JobStatus.COMPLETED) {
      throw new IllegalStateException("Reviews can only be submitted for COMPLETED jobs");
    }

    if (reviewRepository.existsByJobId(job.getId())) {
      throw new IllegalStateException("A review already exists for this job");
    }

    User reviewer = userRepository.findByFirebaseUid(reviewerFirebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (!job.getCreator().getId().equals(reviewer.getId())) {
      throw new IllegalStateException("Only the job creator can submit a review");
    }

    User reviewee = job.getHelper();
    if (reviewee == null) {
      throw new IllegalStateException("No helper found for this job");
    }

    Review review = Review.builder()
        .job(job)
        .reviewer(reviewer)
        .reviewee(reviewee)
        .rating(request.getRating())
        .comment(request.getComment())
        .build();

    Review saved = reviewRepository.save(review);

    // Recalculate helper's reputation score as the average of all their ratings
    Double avgRating = reviewRepository.findAverageRatingByRevieweeId(reviewee.getId());
    if (avgRating != null) {
      reviewee.setReputationScore(avgRating);
      userRepository.save(reviewee);
    }

    return reviewMapper.toResponse(saved);
  }

  public ReviewResponse getReviewByJobId(Long jobId) {
    Review review = reviewRepository.findByJobId(jobId)
        .orElseThrow(() -> new NotFoundException("Review not found for job id: " + jobId));
    return reviewMapper.toResponse(review);
  }
}
