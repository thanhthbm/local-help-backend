package vn.localhelp.core.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.localhelp.core.domain.entity.*;
import vn.localhelp.core.domain.mapper.JobMapper;
import vn.localhelp.core.domain.request.review.ReviewRequest;
import vn.localhelp.core.domain.response.application.ApplicationResponse;
import vn.localhelp.core.domain.response.job.JobDetailResponse;
import vn.localhelp.core.domain.response.job.JobImageResponse;
import vn.localhelp.core.domain.response.progress.ProgressResponse;
import vn.localhelp.core.domain.response.review.ReviewResponse;
import vn.localhelp.core.repository.*;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.request.job.SearchJobRequest;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.specification.JobSpecification;
import vn.localhelp.core.util.DistanceUtil;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.constant.ErrorCode;
import vn.localhelp.core.util.constant.ImageType;
import vn.localhelp.core.util.constant.JobProgress;
import vn.localhelp.core.util.constant.JobStatus;
import vn.localhelp.core.util.error.AppException;
import vn.localhelp.core.util.error.NotFoundException;

import static java.util.stream.Collectors.toList;
@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {
  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final JobApplicationRepository applicationRepository;
  private final ProgressRepository progressRepository;
  private final CloudinaryService cloudinaryService;
  private final ReviewRepository reviewRepository;
  private final JobImageRepository jobImageRepository;
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
              .imageType(vn.localhelp.core.util.constant.ImageType.REQUEST)
              .job(job)
              .build())
          .collect(toList());
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

    if (job.getJobStatus() != JobStatus.OPEN) {
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
    job.setJobStatus(JobStatus.ACCEPTED);

    return jobMapper.toResponse(jobRepository.save(job));
  }

  public ResultPaginationDTO<List<JobResponse>> searchJobs(String keyword, int page, int size) {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    Specification<Job> specification = Specification.allOf(
        JobSpecification.hasJobStatus(JobStatus.OPEN),
        JobSpecification.notCreatedByFirebaseUid(currentUid),
        JobSpecification.hasKeyword(keyword)
    );
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    Page<Job> pageJob = jobRepository.findAll(specification, pageable);

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

  public ResultPaginationDTO<List<JobResponse>> getOpenJob(int page, int size, Long categoryId, Double lat, Double lng){
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
    String currentUid = FirebaseUtil.getCurrentUserUid();
    Specification<Job> specification = Specification.allOf(
        JobSpecification.hasJobStatus(JobStatus.OPEN),
        JobSpecification.notCreatedByFirebaseUid(currentUid),
        JobSpecification.hasCategory(categoryId)
    );
    Page<Job> pageJob = jobRepository.findAll(specification, pageable);

    List<JobResponse> listJobResponse = pageJob.getContent().stream()
        .map(job -> {
          JobResponse response = jobMapper.toResponse(job);
          if (lat != null && lng != null && job.getLatitude() != null && job.getLongitude() != null) {
            response.setDistance(DistanceUtil.calculateDistance(lat, lng, job.getLatitude(), job.getLongitude()));
          }
          return response;
        })
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

    return jobs.stream().map(jobMapper::toResponse).collect(toList());
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

  public ResultPaginationDTO<List<JobResponse>> searchJobs(SearchJobRequest req) {

        if (req.getLatitude() == null || req.getLongitude() == null || req.getMaxDistance() == null) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }
        Boolean hasCategory = !req.getCategoryIds().isEmpty();
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());

        Page<Job> jobPage = jobRepository.searchJobsNearby(
              req.getUserId(),
              req.getLatitude(),
              req.getLongitude(),
              req.getMaxDistance(),
              req.getMinSalary(),
              hasCategory,
              req.getCategoryIds(),
              req.getStartTime(),
              req.getEndTime(),
              req.getKeyword(),
              pageable
        );
        List<JobResponse> dtoJobList = jobPage.getContent().stream()
                                        .map(jobMapper::toResponse)
                                        .toList();

      ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
      meta.setPage(req.getPage());
      meta.setSize(req.getSize());
      meta.setPages(jobPage.getTotalPages());
      meta.setTotal(jobPage.getTotalElements());

      ResultPaginationDTO<List<JobResponse>> result = new ResultPaginationDTO<>();
      result.setMeta(meta);
      result.setResult(dtoJobList);
      return result;
    }

  public List<JobResponse> getFeaturedJobs() {
    String currentUid = FirebaseUtil.getCurrentUserUid();
    Specification<Job> specification = Specification.allOf(
        JobSpecification.hasJobStatus(JobStatus.OPEN),
        JobSpecification.notCreatedByFirebaseUid(currentUid)
    );
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<Job> pageJob = jobRepository.findAll(specification, pageable);
    return pageJob.getContent().stream()
        .map(jobMapper::toResponse)
        .collect(toList());
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
    if (job.getJobImages() == null) {
      job.setJobImages(new ArrayList<>());
    } else {
      job.getJobImages().clear();
    }

    for (String url : imageUrls) {
      job.getJobImages().add(JobImage.builder()
          .imageUrl(url)
          .imageType(vn.localhelp.core.util.constant.ImageType.REQUEST)
          .job(job)
          .build());
    }
  }

  public ResultPaginationDTO<List<JobResponse>> getMyPosts(Long userId, int page, int size) {

      Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

      Page<Job> pageJob = jobRepository.findByCreatorIdOrderByCreatedAtDesc(userId, pageable);

      List<JobResponse> list = pageJob.getContent().stream().map(job -> {
            JobResponse response = jobMapper.toResponse(job);
            response.setDistance(0.0);
            return response;
        }).collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setSize(size);
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        ResultPaginationDTO<List<JobResponse>> result = new ResultPaginationDTO<>();
        result.setMeta(meta);
        result.setResult(list);

        return result;
    }

  public ResultPaginationDTO<List<JobResponse>> getMyTasks(Long helperId, int page, int size, Double helperLat, Double helperLng) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        Page<JobApplication> pageApp = applicationRepository.findByHelperId(helperId, pageable);

        List<JobResponse> list = pageApp.getContent().stream().map(app -> {
            Job job = app.getJob();
            JobResponse response = jobMapper.toResponse(job);
            response.setStatus(JobStatus.valueOf(app.getCurrentProgress().name()));
            if (helperLat != null && helperLng != null && job.getLatitude() != null && job.getLongitude() != null) {
                double dist = DistanceUtil.calculateDistance(
                        helperLat, helperLng, job.getLatitude(), job.getLongitude()
                );
                response.setDistance(dist);
            } else {
                response.setDistance(null);
            }

            return response;
        }).collect(Collectors.toList());

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setSize(size);
        meta.setPages(pageApp.getTotalPages());
        meta.setTotal(pageApp.getTotalElements());

        ResultPaginationDTO<List<JobResponse>> result = new ResultPaginationDTO<>();
        result.setMeta(meta);
        result.setResult(list);

        return result;
    }

    public JobDetailResponse getJobDetail(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        JobResponse jobResponse = jobMapper.toResponse(job);
        jobResponse.setDistance(null);

        List<ProgressResponse> progressResponses = new ArrayList<>();
        boolean isCreator = job.getCreator().getId().equals(currentUserId);


        if (isCreator) {
            if (job.getHelper() != null) {
                applicationRepository.findByJobIdAndHelperId(jobId, job.getHelper().getId())
                        .ifPresent(app -> {
                            progressResponses.addAll(getMappedProgresses(app.getId()));
                        });
            }
        } else {
            applicationRepository.findByJobIdAndHelperId(jobId, currentUserId)
                    .ifPresent(app -> {
                        progressResponses.addAll(getMappedProgresses(app.getId()));
                    });
        }

        if(job.getHelper() != null){
            jobResponse.setHelperId(job.getHelper().getId());
            jobResponse.setHelperName(job.getHelper().getFullName());
            jobResponse.setHelperAvatar(job.getHelper().getAvatarUrl());

            Double rating = job.getHelper().getReputationScore();
            jobResponse.setHelperRating(rating != null ? rating : 0.0);
        }

        return JobDetailResponse.builder()
                .jobInfo(jobResponse)
                .description(job.getDescription())
                .progresses(progressResponses)
                .build();
    }

    private List<ProgressResponse> getMappedProgresses(Long applicationId) {
        List<Progress> history = progressRepository.findByJobApplicationIdOrderByTimestampAsc(applicationId);
        if (history.isEmpty()) return new ArrayList<>();

        List<ProgressResponse> mapped = history.stream().map(p ->
                ProgressResponse.builder()
                        .stepName(p.getName().name())
                        .description(p.getDescription())
                        .time(p.getTimestamp())
                        .isCompleted(true)
                        .isCurrent(false)
                        .build()
        ).collect(Collectors.toList());

        mapped.getLast().setCurrent(true);
        return mapped;
    }


    @Transactional
    public void updateStatusMoving(Long jobId, Long helperId) {
        updateJobAndProgress(jobId, helperId, JobStatus.ON_THE_WAY, "Thợ đang trên đường đến.");
    }

    @Transactional
    public void updateStatusArrived(Long jobId, Long helperId) {
        updateJobAndProgress(jobId, helperId, JobStatus.WORKING, "Thợ đã đến nơi và bắt đầu làm việc.");
    }

    private void updateJobAndProgress(Long jobId, Long helperId, JobStatus newStatus, String description) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (job.getHelper() == null || !job.getHelper().getId().equals(helperId)) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        job.setJobStatus(newStatus);
        jobRepository.save(job);

        JobApplication app = applicationRepository.findByJobIdAndHelperId(jobId, helperId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PARAM));

        app.setCurrentProgress(JobProgress.valueOf(newStatus.name()));
        applicationRepository.save(app);

        Progress progress = new Progress();
        progress.setJobApplication(app);
        progress.setName(JobProgress.valueOf(newStatus.name()));
        progress.setDescription(description);
        progress.setTimestamp(LocalDateTime.now());
        progressRepository.save(progress);
    }

    @Transactional
    public void submitEvidence(Long jobId, List<String> imageUrls) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String url : imageUrls) {
                JobImage jobImage = new JobImage();
                jobImage.setImageUrl(url);
                jobImage.setImageType(ImageType.PROOF);
                jobImage.setJob(job);

                job.getJobImages().add(jobImage);
            }
        }

        job.setJobStatus(JobStatus.PENDING_PAYMENT);

        jobRepository.save(job);
    }

    public void remindPayment(Long jobId, Long helperId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (job.getHelper() == null || !job.getHelper().getId().equals(helperId)) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        if (job.getJobStatus() != JobStatus.PENDING_PAYMENT) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        User creator = job.getCreator();
    }

    @Transactional
    public void confirmPayment(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getCreator().getId().equals(currentUserId)) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        if (job.getJobStatus() != JobStatus.PENDING_PAYMENT) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        job.setJobStatus(JobStatus.COMPLETED);
        jobRepository.save(job);

        JobApplication app = applicationRepository.findByJobIdAndHelperId(jobId, job.getHelper().getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PARAM));

        app.setCurrentProgress(JobProgress.COMPLETED);
        applicationRepository.save(app);

        Progress progress = new Progress();
        progress.setJobApplication(app);
        progress.setName(JobProgress.COMPLETED);

        // Cộng tiền ở đây

        progress.setDescription("Chủ nhà đã xác nhận hoàn thành và thanh toán. Công việc kết thúc.");
        progress.setTimestamp(LocalDateTime.now());
        progressRepository.save(progress);
    }

    @Transactional
    public void reviewHelper(Long jobId, Long currentUserId, ReviewRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getCreator().getId().equals(currentUserId)) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        if (job.getJobStatus() != JobStatus.COMPLETED) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        Review review = new Review();
        review.setJob(job);
        review.setReviewer(job.getCreator());
        review.setReviewee(job.getHelper());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);

        Double avgRating = reviewRepository.getAverageRatingByRevieweeId(job.getHelper().getId());
        User helper = job.getHelper();
        helper.setReputationScore(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        userRepository.save(helper);
    }

    public List<JobImageResponse> getJobEvidence(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getCreator().getId().equals(currentUserId) &&
                (job.getHelper() == null || !job.getHelper().getId().equals(currentUserId))) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        return jobImageRepository.findByJobIdAndImageType(jobId, ImageType.PROOF)
                .stream()
                .map(img -> JobImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .imageType(img.getImageType().name())
                        .build())
                .toList();
    }

    public ReviewResponse getJobReview(Long jobId) {
        jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        Review review = reviewRepository.findByJobId(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PARAM));

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewerName(review.getReviewer().getFullName())
                .reviewerAvatar(review.getReviewer().getAvatarUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
