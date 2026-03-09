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

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface JobMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "jobStatus", ignore = true)
  @Mapping(target = "creator", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "jobImages", ignore = true)
  Job toEntity(CreateJobRequest createJobRequest);

  @Mapping(source = "jobImages", target = "images", qualifiedByName = "mapJobImagesToStrings")
  @Mapping(source = "category.name", target = "categoryName")
  @Mapping(source = "category.iconUrl", target = "categoryIcon")
  @Mapping(source = "creator.fullName", target = "creatorName")
  @Mapping(source = "creator.avatarUrl", target = "creatorAvatar")
  @Mapping(source = "creator.reputationScore", target = "creatorRating")
  JobResponse toResponse(Job job);

  @Named("mapJobImagesToStrings")
  default List<String> mapImages(List<JobImage> jobImages) {
    if (jobImages == null) return Collections.emptyList();
    return jobImages.stream()
        .map(JobImage::getImageUrl)
        .collect(Collectors.toList());
  }
}
