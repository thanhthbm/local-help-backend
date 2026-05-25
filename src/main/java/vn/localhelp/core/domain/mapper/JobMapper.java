package vn.localhelp.core.domain.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.JobImage;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.response.job.JobResponse;
/**
 * MapStruct Mapper chuyển đổi giữa Job entity và các DTO liên quan.
 *
 * <p>uses = {UserMapper.class, CategoryMapper.class}: MapStruct tự động dùng
 * các mapper phụ trợ này để map nested objects (User, Category).</p>
 *
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface JobMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "jobStatus", ignore = true)
  @Mapping(target = "creator", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "jobImages", ignore = true)
  /**
   * Chuyển CreateJobRequest sang Job entity để lưu DB.
   *
   * <p>Các trường được ignore vì sẽ được set thủ công trong JobService:</p>
   * <ul>
   *   <li>id – auto-increment, không set từ request.</li>
   *   <li>createdAt, jobStatus – set trong service (OPEN, LocalDateTime.now()).</li>
   *   <li>creator, category – lookup từ DB sau khi map.</li>
   *   <li>jobImages – xử lý riêng vì có relationship phức tạp.</li>
   * </ul>
   */
  Job toEntity(CreateJobRequest createJobRequest);

  @Mapping(source = "jobStatus", target = "status")
  @Mapping(source = "jobImages", target = "images", qualifiedByName = "mapJobImagesToStrings")
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(source = "category.iconUrl", target = "categoryIcon")
  @Mapping(source = "creator.fullName", target = "creatorName")
  @Mapping(source = "creator.id", target = "creatorId")
  @Mapping(source = "creator.avatarUrl", target = "creatorAvatar")
  @Mapping(source = "creator.reputationScore", target = "creatorRating")
  /**
   * Chuyển Job entity sang JobResponse DTO trả về client.
   *
   * <p>Mapping quan trọng:</p>
   * <ul>
   *   <li>jobStatus → status: đổi tên field.</li>
   *   <li>jobImages → images: dùng @Named("mapJobImagesToStrings") chuyển List<JobImage> → List<String>.</li>
   *   <li>category.name → categoryName, category.iconUrl → categoryIcon.</li>
   *   <li>creator.fullName/id/avatarUrl/reputationScore → creatorName/Id/Avatar/Rating.</li>
   * </ul>
   */
  JobResponse toResponse(Job job);
  /**
   * Chuyển List<JobImage> entity sang List<String> (chỉ lấy imageUrl).
   *
   * <p>Guard null: nếu jobImages == null trả Collections.emptyList() thay vì NullPointerException.
   * @Named("mapJobImagesToStrings") để MapStruct tham chiếu đúng method này.</p>
   *
   * @param jobImages  Danh sách JobImage entity
   * @return           Danh sách URL ảnh dạng String
   */
  @Named("mapJobImagesToStrings")
  default List<String> mapImages(List<JobImage> jobImages) {
    if (jobImages == null) return Collections.emptyList();
    return jobImages.stream()
        .map(JobImage::getImageUrl)
        .collect(Collectors.toList());
  }
}
