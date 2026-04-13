package vn.localhelp.core.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
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
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.constant.ImageType;
import vn.localhelp.core.util.constant.JobStatus;
import vn.localhelp.core.util.error.NotFoundException;
import vn.localhelp.core.util.specification.JobSpecification;

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

  @Transactional
  public JobResponse updateJob(Long id, String currentFirebaseUid, CreateJobRequest request) {
    Job job = getJobOrThrow(id);
    validateCreator(job, currentFirebaseUid);

    if (job.getJobStatus() != JobStatus.OPEN) {
      throw new RuntimeException("Only open jobs can be updated");
    }
    if (request.getTitle() != null) {
      job.setTitle(request.getTitle());
    }
    if (request.getDescription() != null) {
      job.setDescription(request.getDescription());
    }
    if (request.getPrice() != null) {
      job.setPrice(request.getPrice());
    }
    if (request.getAddress() != null) {
      job.setAddress(request.getAddress());
    }
    if (request.getLatitude() != null) {
      job.setLatitude(request.getLatitude());
    }
    if (request.getLongitude() != null) {
      job.setLongitude(request.getLongitude());
    }
    if (request.getCategoryId() != null) {
      Category category = categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new RuntimeException("Category not found"));
      job.setCategory(category);
    }
    if (request.getImageUrls() != null) {
      replaceJobImages(job, request.getImageUrls());
    }

    return jobMapper.toResponse(jobRepository.save(job));
  }

  @Transactional
  public void deleteJob(Long id, String currentFirebaseUid) {
    Job job = getJobOrThrow(id);
    validateCreator(job, currentFirebaseUid);

    if (job.getJobStatus() == JobStatus.IN_PROGRESS || job.getJobStatus() == JobStatus.COMPLETED) {
      throw new RuntimeException("Cannot delete a job that is already in progress or completed");
    }

    jobRepository.delete(job);
  }

  @Transactional
  public JobResponse acceptJob(Long id, String currentFirebaseUid) {
    Job job = getJobOrThrow(id);

    if (job.getCreator() != null && currentFirebaseUid.equals(job.getCreator().getFirebaseUid())) {
      throw new RuntimeException("Creator cannot accept their own job");
    }
    if (job.getJobStatus() != JobStatus.OPEN) {
      throw new RuntimeException("Only open jobs can be accepted");
    }

    User helper = userRepository.findByFirebaseUid(currentFirebaseUid)
        .orElseThrow(() -> new NotFoundException("User not found"));

    job.setHelper(helper);
    job.setJobStatus(JobStatus.ASSIGNED);

    return jobMapper.toResponse(jobRepository.save(job));
  }

  public List<JobResponse> searchJobs(String keyword) {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    Specification<Job> specification = Specification.allOf(
        JobSpecification.hasJobStatus(JobStatus.OPEN),
        JobSpecification.notCreatedByFirebaseUid(currentUid),
        JobSpecification.hasKeyword(keyword)
    );

    return jobRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream()
        .map(jobMapper::toResponse)
        .collect(Collectors.toList());
  }

  public ResultPaginationDTO<List<JobResponse>> getOpenJob(int page, int size){
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    String currentUid = FirebaseUtil.getCurrentUserUid();
    Page<Job> pageJob = jobRepository.findByJobStatus(JobStatus.OPEN, currentUid, pageable);

    List<JobResponse> listJobResponse = pageJob.getContent().stream()
        .map(jobMapper::toResponse)
        .toList();

    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(page);
    meta.setSize(size);
    meta.setPages(pageJob.getTotalPages());
    meta.setTotal(pageJob.getTotalElements());

    ResultPaginationDTO<List<JobResponse>> result = new ResultPaginationDTO<>();
    result.setMeta(meta);
    result.setResult(listJobResponse);

    return result;
  }

  public List<JobResponse> getMyJobs(JobStatus jobStatus){
    String uid = SecurityContextHolder.getContext().getAuthentication().getName();
    User currentUser = userRepository.findByFirebaseUid(uid)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Specification<Job> specification = Specification.allOf(
        JobSpecification.hasCreatorId(currentUser.getId()),
        JobSpecification.hasJobStatus(jobStatus)
    );

    List<Job> jobs = jobRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"));

    return jobs.stream().map(jobMapper::toResponse).collect(Collectors.toList());
  }

  public JobResponse getJobById(Long id) {
    Job job = getJobOrThrow(id);

    return jobMapper.toResponse(job);
  }
  public long countCompletedJobs() {
    return jobRepository.countByJobStatus(JobStatus.COMPLETED);
  }
  public ResultPaginationDTO<List<JobResponse>> getAllJobsForAdmin(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    Page<Job> pageJob = jobRepository.findAll(pageable);
    List<JobResponse> listJobResponse = pageJob.getContent().stream()
            .map(jobMapper::toResponse)
            .toList();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
    meta.setPage(page);
    meta.setSize(size);
    meta.setPages(pageJob.getTotalPages());
    meta.setTotal(pageJob.getTotalElements());
    ResultPaginationDTO<List<JobResponse>> result = new ResultPaginationDTO<>();
    result.setMeta(meta);
    result.setResult(listJobResponse);

    return result;
  }

  private Job getJobOrThrow(Long id) {
    return jobRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Job not found"));
  }

  private void validateCreator(Job job, String currentFirebaseUid) {
    if (job.getCreator() == null || !currentFirebaseUid.equals(job.getCreator().getFirebaseUid())) {
      throw new RuntimeException("You are not allowed to modify this job");
    }
  }

  private void replaceJobImages(Job job, List<String> imageUrls) {
    List<JobImage> images = new ArrayList<>();
    for (String url : imageUrls) {
      images.add(JobImage.builder()
          .imageUrl(url)
          .imageType(ImageType.REQUEST)
          .job(job)
          .build());
    }
    job.setJobImages(images);
  }
}
