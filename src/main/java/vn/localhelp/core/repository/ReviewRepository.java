package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.localhelp.core.domain.entity.Review;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  long countByRevieweeId(Long revieweeId);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :revieweeId")
  Double getAverageRatingByRevieweeId(@Param("revieweeId") Long revieweeId);

  Optional<Review> findByJobId(Long jobId);

  Page<Review> findByRevieweeId(Long revieweeId, Pageable pageable);
}
