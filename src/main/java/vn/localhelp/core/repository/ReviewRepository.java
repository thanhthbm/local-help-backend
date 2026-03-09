package vn.localhelp.core.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.localhelp.core.domain.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  Optional<Review> findByJobId(Long jobId);

  boolean existsByJobId(Long jobId);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.id = :userId")
  Double findAverageRatingByRevieweeId(@Param("userId") Long userId);
}
