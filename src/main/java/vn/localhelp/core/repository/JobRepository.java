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
/**
 * Spring Data JPA Repository cho entity Job.
 *
 * <p>Hỗ trợ tìm kiếm không gian địa lý bằng MySQL native query với hàm
 * ST_Distance_Sphere(POINT(lng, lat), POINT(userLng, userLat)) tính khoảng cách km.</p>
 *
 */
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
  long countByHelperIdAndJobStatus(Long helperId, JobStatus status);

  // Truy vấn danh sách công việc đang mở, loại trừ các job do chính user hiện tại đăng.
  @Query("SELECT j FROM Job j "
      + "WHERE j.jobStatus = :status "
      + "AND j.creator.firebaseUid != :currentUid")
  Page<Job> findByJobStatus(@Param("status") JobStatus status, String currentUid, Pageable pageable);
  long countByJobStatus(JobStatus status);

  // Tìm công việc theo vị trí, khoảng cách, danh mục, thời gian và từ khóa cho màn tìm việc.
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
  /**
   * Tìm kiếm jobs trong bán kính địa lý, lọc theo nhiều điều kiện.
   *
   * <p>Dùng MySQL Native Query với ST_Distance_Sphere() để tính khoảng cách
   * chính xác theo địa cầu (Great-circle distance). Đơn vị: mét.
   * maxDistance truyền vào đơn vị km nên nhân 1000 trong query.</p>
   *
   * <p>countQuery riêng: cần thiết cho phân trang vì query chính có ORDER BY,
   * Spring Data JPA không thể tự tạo count query từ query có ORDER BY.</p>
   *
   * @param userId      ID user (loại bỏ jobs mình đã đăng)
   * @param userLat     Vĩ độ user hiện tại
   * @param userLng     Kinh độ user hiện tại
   * @param maxDistance Bán kính tìm kiếm (km)
   * @param minPrice    Giá tối thiểu (0 = không lọc)
   * @param hasCategory true nếu lọc theo category
   * @param categoryIds Danh sách category ID (chỉ dùng khi hasCategory=true)
   * @param startTime   Lọc jobs từ thời điểm này (null = không giới hạn)
   * @param endTime     Lọc jobs đến thời điểm này (null = không giới hạn)
   * @param keyword     Từ khóa tìm trong title/description (null hoặc rỗng = không lọc)
   * @param pageable    Thông tin phân trang
   * @return            Page<Job> kết quả phân trang
   */
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

  /**
   * Lấy phân trang danh sách jobs mà user đã đăng, sắp xếp mới nhất trước.
   *
   * Spring Data JPA tự sinh query: SELECT j FROM Job j WHERE j.creator.id = ?
   * ORDER BY j.createdAt DESC
   *
   * @param creatorId  ID người đăng việc
   * @param pageable   Thông tin phân trang
   * @return           Page<Job>
   */
  Page<Job> findByCreatorIdOrderByCreatedAtDesc(Long creatorId, Pageable pageable);

    List<Job> findByCreatorIdAndJobStatusAndCreatedAtBetween(Long creatorId, JobStatus status, LocalDateTime start, LocalDateTime end);
    List<Job> findByHelperIdAndJobStatusAndCreatedAtBetween(Long helperId, JobStatus status, LocalDateTime start, LocalDateTime end);

}
