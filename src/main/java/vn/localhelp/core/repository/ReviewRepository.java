package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.localhelp.core.domain.entity.Review;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  /** Đếm tổng số đánh giá mà một người dùng nhận được trên hồ sơ. */
  long countByRevieweeId(Long revieweeId);

  /** Tính điểm rating trung bình của người được đánh giá. */
  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :revieweeId")
  Double getAverageRatingByRevieweeId(@Param("revieweeId") Long revieweeId);

  /** Tìm review gắn với một job cụ thể để tránh tạo trùng đánh giá. */
  Optional<Review> findByJobId(Long jobId);

  /** Lấy danh sách đánh giá hiển thị trên hồ sơ người dùng. */
  Page<Review> findByRevieweeId(Long revieweeId, Pageable pageable);
}
