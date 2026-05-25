package vn.localhelp.core.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.localhelp.core.util.constant.GenderEnum;
import vn.localhelp.core.util.constant.UserRole;
import vn.localhelp.core.util.constant.UserStatus;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
// Entity người dùng; firebaseUid/email liên kết tài khoản backend với Firebase Authentication.
public class User {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  // UID từ Firebase, dùng để xác định user hiện tại khi đăng/sửa/hủy công việc.
  @Column(name = "firebase_uid", unique = true, nullable = false, length = 50)
  private String firebaseUid;

  // Email cũng là khóa chính của luồng đổi mật khẩu bằng OTP.
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "full_name", length = 100)
  private String fullName;

  @Column(name = "avatar_url", columnDefinition = "TEXT")
  private String avatarUrl;

  @Column(length = 15)
  private String phone;

  @Builder.Default
  @Column(name = "reputation_score")
  private Double reputationScore = 5.0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private GenderEnum gender = GenderEnum.UNKNOWN;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  // Các công việc user đã đăng với vai trò chủ việc.
  @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
  private List<Job> jobsCreated;

  // Các công việc user đã nhận/thực hiện với vai trò thợ.
  @OneToMany(mappedBy = "helper", fetch = FetchType.LAZY)
  private List<Job> jobsCompleted;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @Builder.Default
  @Column(name = "is_new")
  private Boolean isNew = false;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

}
