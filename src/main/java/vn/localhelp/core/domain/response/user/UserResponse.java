package vn.localhelp.core.domain.response.user;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import vn.localhelp.core.util.constant.GenderEnum;
import vn.localhelp.core.util.constant.UserRole;
import vn.localhelp.core.util.constant.UserStatus;

@Data
@Builder
public class UserResponse {
  /** Id nội bộ của user trong database. */
  private Long id;
  /** UID do Firebase Authentication cấp, dùng để đối chiếu người đang đăng nhập. */
  private String firebaseUid;

  private String fullName;
  private String email;
  private String phone;
  private String avatarUrl;

  private Double reputationScore;
  // private int jobCount;
  private UserRole role;
  private UserStatus status;
  private GenderEnum gender;
  private String bio;
  private Boolean isNew;
  /** Số job người dùng đã hoàn thành với vai trò helper. */
  private int completedJobs;
  /** Tổng số review mà người dùng nhận được. */
  private int totalReviews;
  /** Điểm đánh giá trung bình đã làm tròn 1 chữ số thập phân. */
  private double averageRating;
  private double responseRate; // Tỷ lệ phản hồi tin nhắn

  private LocalDateTime createdAt;
}
