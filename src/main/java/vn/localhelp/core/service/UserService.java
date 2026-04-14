package vn.localhelp.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.UserMapper;
import vn.localhelp.core.domain.request.user.UpdateProfileRequest;
import vn.localhelp.core.domain.response.user.UserResponse;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.ReviewRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.constant.UserRole;
import vn.localhelp.core.util.error.NotFoundException;
import vn.localhelp.core.util.constant.JobStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.util.constant.UserStatus;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JobRepository jobRepository;
  private final ReviewRepository reviewRepository;
  private final CloudinaryService cloudinaryService;

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

  @Transactional
  public UserResponse updateProfile(String firebaseUid, UpdateProfileRequest request, MultipartFile avatarFile) {
    User user = userRepository.findByFirebaseUid(firebaseUid)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

    // Cập nhật thông tin text nếu có
    if (request != null) {
      if (request.getFullName() != null) user.setFullName(request.getFullName());
      if (request.getPhone() != null)    user.setPhone(request.getPhone());
      if (request.getGender() != null)   user.setGender(request.getGender());
      if (request.getBio() != null)      user.setBio(request.getBio());
    }

    // Upload avatar nếu có
    if (avatarFile != null && !avatarFile.isEmpty()) {
      try {
        String avatarUrl = cloudinaryService.uploadImage(avatarFile);
        user.setAvatarUrl(avatarUrl);
      } catch (Exception e) {
        throw new RuntimeException("Không thể tải lên ảnh đại diện: " + e.getMessage(), e);
      }
    }

    if (Boolean.FALSE.equals(user.getIsNew())) {
      user.setIsNew(true);
    }

    User saved = userRepository.save(user);

    Long userId = saved.getId();
    int completedJobs = (int) jobRepository.countByHelperIdAndJobStatus(userId, JobStatus.COMPLETED);
    int totalReviews  = (int) reviewRepository.countByRevieweeId(userId);
    Double avgRating  = reviewRepository.getAverageRatingByRevieweeId(userId);
    double finalAvg   = (avgRating != null) ? Math.round(avgRating * 10.0) / 10.0 : 0.0;

    return userMapper.toResponseWithStats(saved, completedJobs, totalReviews, finalAvg, 0.98);
  }

  public long countTotalUsers() {
    return userRepository.countByRole(UserRole.USER);
  }

  public ResultPaginationDTO<List<UserResponse>> getAllUsersForAdmin(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

    Page<User> pageUser = userRepository.findAll(pageable);

    List<UserResponse> listUserResponse = pageUser.getContent().stream()
            .map(userMapper::toResponse)
            .toList();

    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(page);
    meta.setSize(size);
    meta.setPages(pageUser.getTotalPages());
    meta.setTotal(pageUser.getTotalElements());

    ResultPaginationDTO<List<UserResponse>> result = new ResultPaginationDTO<>();
    result.setMeta(meta);
    result.setResult(listUserResponse);

    return result;
  }

  @Transactional
  public UserResponse updateUserStatus(Long userId, UserStatus newStatus) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + userId));

    user.setStatus(newStatus);
    User updatedUser = userRepository.save(user);

    return userMapper.toResponse(updatedUser);
  }
}
