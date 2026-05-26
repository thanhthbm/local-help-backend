package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.JobApplication;
import vn.localhelp.core.util.constant.JobProgress;

import java.util.List;
import java.util.Optional;
/**
 * Spring Data JPA Repository cho entity JobApplication.
 *
 * <p>Cung cấp các truy vấn để quản lý ứng tuyển công việc.</p>
 *
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    /**
     * Kiểm tra helper đã ứng tuyển job này chưa.
     * Dùng để chặn ứng tuyển trùng lặp trước khi tạo mới JobApplication.
     *
     * @return true nếu đã ứng tuyển, false nếu chưa
     */
    boolean existsByJobIdAndHelperId(Long jobId, Long helperId);
    /**
     * Lấy phân trang danh sách các ứng tuyển của một helper.
     * Dùng cho tính năng xem 'Việc đã nhận' từ góc nhìn của helper.
     *
     * @param helperId  ID của helper
     * @param pageable  Thông tin phân trang
     * @return          Page<JobApplication>
     */
    Page<JobApplication> findByHelperId(Long helperId, Pageable pageable);
    /**
     * Tìm một đơn ứng tuyển cụ thể theo job và helper.
     *
     * <p>Dùng khi cập nhật tiến trình công việc hoặc lấy timeline của helper đã ứng tuyển.</p>
     *
     * @param jobId    ID công việc
     * @param helperId ID helper
     * @return         Optional<JobApplication>, rỗng nếu helper chưa ứng tuyển job này
     */
    Optional<JobApplication> findByJobIdAndHelperId(Long jobId, Long helperId);
    /**
     * Lấy danh sách ứng tuyển theo jobId và trạng thái hiện tại.
     * Dùng để lấy list các helper đang ở trạng thái APPLIED cho creator xem xét.
     *
     * @param jobId            ID công việc
     * @param currentProgress  Trạng thái cần lọc (vd: JobProgress.APPLIED)
     * @return                 List<JobApplication>
     */
    List<JobApplication> findByJobIdAndCurrentProgress(Long jobId, JobProgress currentProgress);
    /**
     * Lấy toàn bộ đơn ứng tuyển của một công việc.
     *
     * <p>Được dùng trong chức năng hủy công việc để cập nhật các application liên quan
     * sang trạng thái CANCELLED.</p>
     *
     * @param jobId ID công việc
     * @return      Danh sách JobApplication của công việc
     */
    List<JobApplication> findByJobId(Long jobId);
}
