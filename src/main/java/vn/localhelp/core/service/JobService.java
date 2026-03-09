package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.JobImage;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.JobMapper;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.repository.CategoryRepository;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.NotFoundException;
import vn.localhelp.core.util.constant.ImageType;
import vn.localhelp.core.util.constant.JobStatus;

@Service
@RequiredArgsConstructor
public class JobService {
  private static final Set<JobStatus> STOPPABLE_STATUSES = Set.of(
      JobStatus.OPEN, JobStatus.IN_PROGRESS, JobStatus.ASSIGNED);

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

  @Transactional
  public JobResponse stopJob(String currentFirebaseUid, Long jobId) {
    User currentUser = userRepository.findByFirebaseUid(currentFirebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new NotFoundException("Job not found"));

    if (!job.getCreator().getId().equals(currentUser.getId())) {
      throw new IllegalStateException("Only the job creator can stop this job");
    }

    if (!STOPPABLE_STATUSES.contains(job.getJobStatus())) {
      throw new IllegalStateException("Job cannot be stopped in its current status: " + job.getJobStatus());
    }

    job.setJobStatus(JobStatus.CANCELLED);
    Job savedJob = jobRepository.save(job);

    return jobMapper.toResponse(savedJob);
  }
}
