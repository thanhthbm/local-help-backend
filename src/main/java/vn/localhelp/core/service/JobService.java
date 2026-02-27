package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import java.util.List;
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

  public List<JobResponse> getAllJobs(JobStatus status, Long categoryId) {
    List<Job> jobs;
    if (status != null) {
      jobs = jobRepository.findByJobStatus(status);
    } else if (categoryId != null) {
      jobs = jobRepository.findByCategoryId(categoryId);
    } else {
      jobs = jobRepository.findAll();
    }
    return jobs.stream().map(jobMapper::toResponse).toList();
  }

  public JobResponse getJobById(Long id) {
    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Job not found with id: " + id));
    return jobMapper.toResponse(job);
  }

  @Transactional
  public JobResponse acceptJob(Long jobId, String helperFirebaseUid) {
    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new NotFoundException("Job not found with id: " + jobId));

    if (job.getJobStatus() != JobStatus.OPEN) {
      throw new IllegalStateException("Only OPEN jobs can be accepted");
    }

    User helper = userRepository.findByFirebaseUid(helperFirebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (job.getCreator().getId().equals(helper.getId())) {
      throw new IllegalStateException("Creator cannot accept their own job");
    }

    job.setHelper(helper);
    job.setJobStatus(JobStatus.ASSIGNED);

    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Transactional
  public JobResponse completeJob(Long jobId, String currentFirebaseUid) {
    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new NotFoundException("Job not found with id: " + jobId));

    if (job.getJobStatus() != JobStatus.ASSIGNED && job.getJobStatus() != JobStatus.IN_PROGRESS) {
      throw new IllegalStateException("Only ASSIGNED or IN_PROGRESS jobs can be completed");
    }

    User currentUser = userRepository.findByFirebaseUid(currentFirebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (!job.getCreator().getId().equals(currentUser.getId())) {
      throw new IllegalStateException("Only the job creator can mark a job as completed");
    }

    job.setJobStatus(JobStatus.COMPLETED);

    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Transactional
  public JobResponse cancelJob(Long jobId, String currentFirebaseUid) {
    Job job = jobRepository.findById(jobId)
        .orElseThrow(() -> new NotFoundException("Job not found with id: " + jobId));

    if (job.getJobStatus() == JobStatus.COMPLETED || job.getJobStatus() == JobStatus.CANCELLED) {
      throw new IllegalStateException("Completed or already cancelled jobs cannot be cancelled");
    }

    User currentUser = userRepository.findByFirebaseUid(currentFirebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (!job.getCreator().getId().equals(currentUser.getId())) {
      throw new IllegalStateException("Only the job creator can cancel a job");
    }

    job.setJobStatus(JobStatus.CANCELLED);

    return jobMapper.toResponse(jobRepository.save(job));
  }
}
