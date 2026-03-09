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
  private Long id;
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
  private int completedJobs;
  private int totalReviews;
  private double averageRating;
  private double responseRate; // Tỷ lệ phản hồi tin nhắn

  private LocalDateTime createdAt;
}