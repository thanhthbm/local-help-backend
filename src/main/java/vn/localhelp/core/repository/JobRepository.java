package vn.localhelp.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.util.constant.JobStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
  long countByHelperIdAndJobStatus(Long helperId, JobStatus status);

  @Query("SELECT j FROM Job j "
      + "WHERE j.jobStatus = :status "
      + "AND j.creator.firebaseUid != :currentUid")
  Page<Job> findByJobStatus(@Param("status") JobStatus status, String currentUid, Pageable pageable);
  long countByJobStatus(JobStatus status);

  @Query(value = "SELECT * FROM jobs j WHERE " +
                   "j.job_status = 'OPEN' " +
                   "AND j.creator_id != :userId " +
                   "AND j.price >= :minPrice " +
                   "AND (:hasCategory = 0 OR j.category_id IN (:categoryIds)) " +
                   "AND (:startTime IS NULL OR j.created_at >= :startTime) " +
                   "AND (:endTime IS NULL OR j.created_at <= :endTime) " +
                   "AND (:keyword IS NULL OR :keyword = '' OR j.title LIKE CONCAT('%', :keyword, '%') " +
                   "OR j.description LIKE CONCAT('%', :keyword, '%')) " +

                   "AND ST_Distance_Sphere(POINT(j.longitude, j.latitude), POINT(:userLng, :userLat)) <= (:maxDistance * 1000) " +
                   "ORDER BY ST_Distance_Sphere(POINT(j.longitude, j.latitude), POINT(:userLng, :userLat)) ASC",
            countQuery = "SELECT count(*) FROM jobs j WHERE " +
                    "j.job_status = 'OPEN' " +
                    "AND j.creator_id != :userId " +
                    "AND j.price >= :minPrice " +
                    "AND (:hasCategory = 0 OR j.category_id IN (:categoryIds)) " +
                    "AND (:startTime IS NULL OR j.created_at >= :startTime) " +
                    "AND (:endTime IS NULL OR j.created_at <= :endTime) " +
                    "AND (:keyword IS NULL OR :keyword = '' OR j.title LIKE CONCAT('%', :keyword, '%') " +
                    "OR j.description LIKE CONCAT('%', :keyword, '%')) " +
                    "AND ST_Distance_Sphere(POINT(j.longitude, j.latitude), POINT(:userLng, :userLat)) <= (:maxDistance * 1000) ",
            nativeQuery = true)
  Page<Job> searchJobsNearby(
            @Param("userId") Long userId,
            @Param("userLat") Double userLat,
            @Param("userLng") Double userLng,
            @Param("maxDistance") Double maxDistance,
            @Param("minPrice") Double minPrice,
            @Param("hasCategory") Boolean hasCategory,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("keyword") String keyword,
            Pageable pageable
  );

    Page<Job> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    List<Job> findByCreatorIdAndJobStatusAndCreatedAtBetween(Long creatorId, JobStatus status, LocalDateTime start, LocalDateTime end);
    List<Job> findByHelperIdAndJobStatusAndCreatedAtBetween(Long helperId, JobStatus status, LocalDateTime start, LocalDateTime end);

}
