package vn.localhelp.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.UserMapper;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.ReviewRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.error.NotFoundException;
import vn.localhelp.core.util.constant.JobStatus;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JobRepository jobRepository;
  private final ReviewRepository reviewRepository;

  public UserResponse getMyProfile(String firebaseUid) {
    User user = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    Long userId = user.getId();

    int completedJobs = (int) jobRepository.countByHelperIdAndJobStatus(userId, JobStatus.COMPLETED);
    int totalReviews = (int) reviewRepository.countByRevieweeId(userId);
    Double avgRating = reviewRepository.getAverageRatingByRevieweeId(userId);
    double finalAvgRating = (avgRating != null) ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    double responseRate = 0.98; // phake

    return userMapper.toResponseWithStats(user, completedJobs, totalReviews, finalAvgRating, responseRate);
  }
}
