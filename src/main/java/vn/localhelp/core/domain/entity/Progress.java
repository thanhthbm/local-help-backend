package vn.localhelp.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import vn.localhelp.core.util.constant.JobProgress;

import java.time.LocalDateTime;
/**
 * JPA Entity ánh xạ bảng 'progress' – lưu lịch sử thay đổi trạng thái của JobApplication.
 *
 * <p>Mỗi bản ghi Progress là một bước trong timeline của công việc:</p>
 * <ul>
 *   <li>APPLIED    – Helper gửi yêu cầu nhận việc.</li>
 *   <li>ACCEPTED   – Creator chấp nhận helper.</li>
 *   <li>IN_PROGRESS – Helper bắt đầu thực hiện.</li>
 *   <li>COMPLETED  – Công việc hoàn thành, chờ thanh toán và review.</li>
 *   <li>CANCELLED  – Bị hủy bởi creator hoặc helper.</li>
 * </ul>
 *
 * <p>timestamp: thời điểm xảy ra thay đổi trạng thái, dùng để hiển thị timeline.</p>
 *
 */
@Entity
@Table(name = "progress")
@Data
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private JobProgress name;

    @Column(name = "description")
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}