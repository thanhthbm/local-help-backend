package vn.localhelp.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.localhelp.core.domain.entity.Progress;

import java.util.List;
/**
 * Spring Data JPA Repository cho entity Progress.
 *
 */
@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    /**
     * Lấy danh sách progress của một JobApplication, sắp xếp tăng dần theo thời gian.
     *
     * <p>Sắp xếp ASC (cũ nhất trước) để hiển thị timeline đúng thứ tự chronological:
     * APPLIED (đầu) → ... → COMPLETED (cuối).</p>
     *
     * @param applicationId  ID của JobApplication
     * @return               List<Progress> sắp xếp theo timestamp ASC
     */
    List<Progress> findByJobApplicationIdOrderByTimestampAsc(Long applicationId);
}
