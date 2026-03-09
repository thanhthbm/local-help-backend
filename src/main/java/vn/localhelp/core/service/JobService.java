package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.JobImage;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.JobMapper;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.repository.CategoryRepository;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.constant.ImageType;
import vn.localhelp.core.util.constant.JobStatus;

@Service
@RequiredArgsConstructor
public class JobService {
  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final JobMapper jobMapper;

  @Transactional
  public JobResponse createJob(String currentFirebaseUid, CreateJobRequest createJobRequest) {
    User creator = userRepository.findByFirebaseUid(currentFirebaseUid)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Category category = categoryRepository.findById(createJobRequest.getCategoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));

    Job job = jobMapper.toEntity(createJobRequest);

    job.setCreator(creator);
    job.setCategory(category);
    job.setJobStatus(JobStatus.OPEN);
    job.setCreatedAt(LocalDateTime.now());

    if (createJobRequest.getImageUrls() != null) {
      List<JobImage> images = createJobRequest.getImageUrls().stream()
          .map(url -> JobImage.builder()
              .imageUrl(url)
              .imageType(ImageType.REQUEST)
              .job(job)
              .build())
          .toList();
      job.setJobImages(images);
    }

    Job savedJob = jobRepository.save(job);

    return jobMapper.toResponse(savedJob);
  }

  public ResultPaginationDTO getOpenJob(int page, int size){
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

    Page<Job> pageJob = jobRepository.findByJobStatus(JobStatus.OPEN, pageable);

    List<JobResponse> listJobResponse = pageJob.getContent().stream()
        .map(jobMapper::toResponse)
        .toList();

    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(page);
    meta.setSize(size);
    meta.setPages(pageJob.getTotalPages());
    meta.setTotal(pageJob.getTotalElements());

    return ResultPaginationDTO.builder()
        .meta(meta)
        .result(listJobResponse)
        .build();
  }
}
