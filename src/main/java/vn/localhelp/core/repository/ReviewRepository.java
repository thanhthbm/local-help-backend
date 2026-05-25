package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.localhelp.core.domain.entity.Review;

import java.util.Optional;
/**
 * Spring Data JPA Repository cho entity Review.
 *
 * <p>Cung cấp các phương thức truy vấn đánh giá theo reviewee và job,
 * bao gồm phân trang và tính điểm trung bình.</p>
 *
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
  /**
   * Đếm tổng số đánh giá mà một người dùng đã nhận.
   * Dùng để hiển thị thống kê trên profile người dùng.
   *
   * @param revieweeId  ID người được đánh giá
   * @return            Số lượng reviews (long)
   */
  long countByRevieweeId(Long revieweeId);
  /**
   * Tính điểm đánh giá trung bình của một người dùng.
   *
   * <p>JPQL: SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :revieweeId
   * Kết quả được dùng để cập nhật User.reputationScore sau mỗi lần có review mới.</p>
   *
   * @param revieweeId  ID người được đánh giá
   * @return            Điểm trung bình kiểu Double, null nếu chưa có review nào
   */
  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :revieweeId")
  Double getAverageRatingByRevieweeId(@Param("revieweeId") Long revieweeId);
  /**
   * Tìm review theo job ID.
   *
   * <p>Dùng để kiểm tra xem job đã có review chưa trước khi tạo mới,
   * tránh vi phạm ràng buộc UNIQUE trên cột job_id trong bảng reviews.
   * Trả Optional.empty() nếu job chưa được đánh giá.</p>
   *
   * @param jobId  ID của công việc
   * @return       Optional<Review>
   */
  Optional<Review> findByJobId(Long jobId);
  /**
   * Lấy phân trang danh sách reviews của người được đánh giá.
   *
   * Spring Data JPA tự sinh câu query: SELECT r FROM Review r WHERE r.reviewee.id = ?
   * Pageable chứa thông tin trang, kích thước và sort (createdAt DESC).
   *
   * @param revieweeId  ID người được đánh giá
   * @param pageable    Thông tin phân trang và sắp xếp
   * @return            Page<Review> kết quả phân trang
   */
  Page<Review> findByRevieweeId(Long revieweeId, Pageable pageable);
}
