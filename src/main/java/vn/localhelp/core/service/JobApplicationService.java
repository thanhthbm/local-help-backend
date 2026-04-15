package vn.localhelp.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.JobApplication;
import vn.localhelp.core.domain.entity.Progress;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.response.application.ApplicationResponse;
import vn.localhelp.core.repository.JobApplicationRepository;
import vn.localhelp.core.repository.JobRepository;
import vn.localhelp.core.repository.ProgressRepository;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.constant.ErrorCode;
import vn.localhelp.core.util.constant.JobProgress;
import vn.localhelp.core.util.constant.JobStatus;
import vn.localhelp.core.util.error.AppException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobApplicationService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository applicationRepository;
    private final ProgressRepository progressRepository;

    public JobApplicationService(JobRepository jobRepository,
                                 UserRepository userRepository,
                                 JobApplicationRepository applicationRepository,
                                 ProgressRepository progressRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.progressRepository = progressRepository;
    }

    @Transactional
    public void applyForJob(Long jobId, Long helperId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!"OPEN".equals(job.getJobStatus().name())) {
            throw new AppException(ErrorCode.JOB_NOT_OPEN);
        }

        if (job.getCreator().getId().equals(helperId)) {
            throw new AppException(ErrorCode.CANNOT_APPLY_OWN_JOB);
        }

        if (applicationRepository.existsByJobIdAndHelperId(jobId, helperId)) {
            throw new AppException(ErrorCode.ALREADY_APPLIED);
        }

        User helper = userRepository.findById(helperId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setHelper(helper);
        application.setCurrentProgress(JobProgress.APPLIED);
        application.setCreatedAt(LocalDateTime.now());

        application = applicationRepository.save(application);

        Progress progress = new Progress();
        progress.setJobApplication(application);
        progress.setName(JobProgress.APPLIED);
        progress.setDescription("Thợ đã gửi yêu cầu nhận việc, chờ chủ nhà xác nhận.");
        progress.setTimestamp(LocalDateTime.now());

        progressRepository.save(progress);
    }

    public List<ApplicationResponse> getApplicationsForJob(Long jobId, Long currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getCreator().getId().equals(currentUserId)) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        List<JobApplication> applications = applicationRepository.findByJobIdAndCurrentProgress(jobId, JobProgress.APPLIED);

        return applications.stream().map(app -> {
            User helper = app.getHelper();
            return ApplicationResponse.builder()
                    .applicationId(app.getId())
                    .helperId(helper.getId())
                    .helperName(helper.getFullName())
                    .helperAvatar(helper.getAvatarUrl())
                    .helperRating(helper.getReputationScore())
                    .status(app.getCurrentProgress().name())
                    .appliedAt(app.getCreatedAt())
                    .build();
        }).toList();
    }

    @Transactional
    public void acceptHelper(Long applicationId, Long currentUserId) {
        JobApplication acceptedApp = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PARAM));

        Job job = acceptedApp.getJob();

        if (!job.getCreator().getId().equals(currentUserId)) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        if (job.getJobStatus() != JobStatus.OPEN) {
            throw new AppException(ErrorCode.INVALID_PARAM);
        }

        job.setHelper(acceptedApp.getHelper());
        job.setJobStatus(JobStatus.ACCEPTED);
        jobRepository.save(job);

        List<JobApplication> allApps = applicationRepository.findByJobId(job.getId());

        for (JobApplication app : allApps) {
            if (app.getId().equals(applicationId)) {
                app.setCurrentProgress(JobProgress.ACCEPTED);
                applicationRepository.save(app);

                Progress progress = new Progress();
                progress.setJobApplication(app);
                progress.setName(JobProgress.ACCEPTED);
                progress.setDescription("Chủ nhà đã xác nhận chọn bạn, hãy chuẩn bị di chuyển.");
                progress.setTimestamp(LocalDateTime.now());
                progressRepository.save(progress);
            } else {
                app.setCurrentProgress(JobProgress.REJECTED);
                applicationRepository.save(app);
            }
        }
    }
}
