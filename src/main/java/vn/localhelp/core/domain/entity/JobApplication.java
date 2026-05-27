package vn.localhelp.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.localhelp.core.util.constant.JobProgress;

import java.time.LocalDateTime;
import java.util.List;
/**
 * JPA Entity ánh xạ bảng 'job_application' – lưu thông tin ứng tuyển công việc.
 *
 * <p>Mỗi JobApplication là một lần helper ứng tuyển vào một job.
 * Một job có thể có nhiều JobApplication (nhiều helper ứng tuyển),
 * nhưng chỉ một helper được chấp nhận (creator accept).</p>
 *
 * <p>currentProgress: trạng thái hiện tại của ứng tuyển này.
 * APPLIED → ACCEPTED → IN_PROGRESS → COMPLETED | CANCELLED</p>
 *
 * <p>progressHistory: @OneToMany CascadeType.ALL – khi xóa JobApplication,
 * toàn bộ Progress liên quan cũng bị xóa (orphanRemoval = true).</p>
 *
 */

@Entity
@Table(name = "job_application")
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "helper_id", nullable = false)
    private User helper;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_progress")
    private JobProgress currentProgress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progress> progressHistory;
}