package vn.localhelp.core.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.request.job.SearchJobRequest;
import vn.localhelp.core.domain.request.review.ReviewRequest;
import vn.localhelp.core.domain.response.application.ApplicationResponse;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.domain.response.job.JobDetailResponse;
import vn.localhelp.core.domain.response.job.JobImageResponse;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.domain.response.review.ReviewResponse;
import vn.localhelp.core.service.JobApplicationService;
import vn.localhelp.core.service.JobService;
import vn.localhelp.core.util.CustomUserDetails;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.annotation.ApiMessage;
import vn.localhelp.core.util.constant.JobStatus;
/**
 * REST Controller quản lý toàn bộ vòng đời của công việc (Job) trong LocalHelp.
 *
 * <p>Endpoint liên quan đến lịch sử (Hoàng Minh Trọng):</p>
 * <ul>
 *   <li>GET /api/jobs/my-jobs  – Việc người dùng đã đăng (là creator).</li>
 *   <li>GET /api/jobs/my-tasks – Việc người dùng đã nhận/đang làm (là helper).</li>
 * </ul>
 *
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;
  private final JobApplicationService jobApplicationService;

  // Use case đăng công việc: nhận request từ Android, lấy Firebase UID hiện tại và chuyển xuống service.
  @PostMapping
  @ApiMessage("Create a new job successfully")
  public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest){
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.createJob(currentFirebaseUid, createJobRequest));
  }

  // Use case cập nhật công việc: chỉ chủ bài đăng hiện tại được phép sửa thông tin công việc.
  @PutMapping("/{id}")
  @ApiMessage("Update job successfully")
  public ResponseEntity<JobResponse> updateJob(
      @PathVariable Long id,
      @RequestBody CreateJobRequest request
  ) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.updateJob(id, currentFirebaseUid, request));
  }

  // Use case hủy công việc: controller gọi service để chuyển trạng thái công việc sang CANCELLED.
  @DeleteMapping("/{id}")
  @ApiMessage("Delete job successfully")
  public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    jobService.deleteJob(id, currentFirebaseUid);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/accept")
  @ApiMessage("Accept job successfully")
  public ResponseEntity<JobResponse> acceptJob(@PathVariable Long id) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.acceptJob(id, currentFirebaseUid));
  }

  @ApiMessage("Fetch open jobs successfully")
  @GetMapping
  public ResponseEntity<ResultPaginationDTO<List<JobResponse>>> getOpenJobs(
      @RequestParam(defaultValue = "1") int current,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Double lat,
      @RequestParam(required = false) Double lng
  ){
    return ResponseEntity.ok(jobService.getOpenJob(current, pageSize, categoryId, lat, lng));
  }
    /**
     * Lấy danh sách công việc mà user hiện tại đã đăng (với vai trò Creator).
     *
     * <p>Lọc theo status nếu có (OPEN/IN_PROGRESS/COMPLETED/CANCELLED).
     * Nếu không truyền status → trả tất cả jobs của creator.</p>
     *
     * <p>Firebase UID được lấy từ SecurityContext qua FirebaseUtil.getCurrentUserUid().</p>
     *
     * @param status  (Optional) Trạng thái cần lọc, null = lấy tất cả
     * @return        List<JobResponse> danh sách việc đã đăng
     */
    @GetMapping("/my-jobs")
  @ApiMessage("Fetch my jobs successfully")
  public ResponseEntity<List<JobResponse>> getMyJobs(
      @RequestParam(required = false) JobStatus status
  ){
    List<JobResponse> jobs = jobService.getMyJobs(status);
    return ResponseEntity.ok(jobs);
  }
  @PostMapping("/search")
  @ApiMessage("search")
  public ResponseEntity<ResultPaginationDTO<List<JobResponse>>> searchJobs(
          @Valid
          @RequestBody SearchJobRequest request,
          @AuthenticationPrincipal CustomUserDetails currentUser) {
      request.setUserId(currentUser.getUserEntity().getId());
      ResultPaginationDTO<List<JobResponse>> results = jobService.searchJobs(request);
      return ResponseEntity.ok(results);
  }

  // Lấy chi tiết công việc để Android hiển thị và mở form cập nhật.
  @GetMapping("/{id}")
  @ApiMessage("Get job by id")
  public ResponseEntity<JobResponse> getJobById(@PathVariable Long id){
      return ResponseEntity.ok(jobService.getJobById(id));
  }

  @GetMapping("/jobs-completed")
  @ApiMessage("Lấy số lượng công việc đã hoàn thành thành công")
  public ResponseEntity<Long> countCompletedJobs() {
        long count = jobService.countCompletedJobs();
        return ResponseEntity.ok(count);
  }

  @GetMapping("/admin/all")
  @PreAuthorize("hasRole('ADMIN')")
  @ApiMessage("Lấy danh sách tất cả công việc cho Admin thành công")
  public ResponseEntity<ResultPaginationDTO<List<JobResponse>>> getAllJobsForAdmin(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize
  ){return ResponseEntity.ok(jobService.getAllJobsForAdmin(current, pageSize));}

  @GetMapping("/featured")
  @ApiMessage("Fetch featured jobs successfully")
  public ResponseEntity<List<JobResponse>> getFeaturedJobs() {
    return ResponseEntity.ok(jobService.getFeaturedJobs());
  }

  @PostMapping("/{jobId}/apply")
  @ApiMessage("Gửi yêu cầu nhận việc thành công")
  public void applyForJob(
          @PathVariable Long jobId,
          @AuthenticationPrincipal CustomUserDetails currentUser) {
      User currUser = currentUser.getUserEntity();
      jobApplicationService.applyForJob(jobId, currUser.getId());
  }

    @GetMapping("/my-posts")
    @ApiMessage("Lấy danh sách việc đã đăng thành công")
    public ResponseEntity<ResultPaginationDTO<List<JobResponse>>> getMyPosts(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ResultPaginationDTO<List<JobResponse>> result =
                jobService.getMyPosts(currentUser.getUserEntity().getId(), current, pageSize);

        return ResponseEntity.ok(result);
    }
    /**
     * Lấy danh sách công việc mà user hiện tại đã nhận (với vai trò Helper).
     *
     * <p>Phân biệt với my-jobs: my-tasks lọc theo helper_id thay vì creator_id.
     * Kết quả gồm các job đã được creator chấp nhận helper hiện tại.</p>
     *
     * @param current   Trang hiện tại (bắt đầu từ 1)
     * @param pageSize  Số phần tử mỗi trang
     * @param lat       (Optional) Vĩ độ của user để tính khoảng cách
     * @param lng       (Optional) Kinh độ của user để tính khoảng cách
     * @return          ResultPaginationDTO<List<JobResponse>>
     */
    @GetMapping("/my-tasks")
    @ApiMessage("Lấy danh sách việc đã nhận thành công")
    public ResponseEntity<ResultPaginationDTO<List<JobResponse>>> getMyTasks(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ResultPaginationDTO<List<JobResponse>> result =
                jobService.getMyTasks(currentUser.getUserEntity().getId(), current, pageSize, lat, lng);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/detail")
    @ApiMessage("Lấy chi tiết công việc và tiến độ thành công")
    public ResponseEntity<JobDetailResponse> getJobDetailTracking(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(jobService.getJobDetail(id, currentUser.getUserEntity().getId()));
    }

    @GetMapping("/{jobId}/applications")
    @ApiMessage("Lấy danh sách thợ ứng tuyển thành công")
    public ResponseEntity<List<ApplicationResponse>> getApplications(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<ApplicationResponse> list = jobApplicationService.getApplicationsForJob(
                jobId,
                currentUser.getUserEntity().getId()
        );
        return ResponseEntity.ok(list);
    }

    @PostMapping("/applications/{applicationId}/accept")
    @ApiMessage("Chọn thợ thành công")
    public ResponseEntity<Void> acceptApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        jobApplicationService.acceptHelper(applicationId, currentUser.getUserEntity().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{jobId}/status/moving")
    @ApiMessage("Cập nhật trạng thái đang di chuyển thành công")
    public ResponseEntity<Void> statusMoving(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        jobService.updateStatusMoving(jobId, currentUser.getUserEntity().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{jobId}/status/arrived")
    @ApiMessage("Cập nhật trạng thái đã đến nơi thành công")
    public ResponseEntity<Void> statusArrived(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        jobService.updateStatusArrived(jobId, currentUser.getUserEntity().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{jobId}/submit-evidence")
    public ResponseEntity<Void> submitEvidence(
            @PathVariable Long jobId,
            @RequestBody List<String> imageUrls) {

        jobService.submitEvidence(jobId, imageUrls);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/{jobId}/remind-payment")
    @ApiMessage("Đã gửi thông báo nhắc nhở khách hàng")
    public ResponseEntity<Void> remindPayment(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        jobService.remindPayment(jobId, currentUser.getUserEntity().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{jobId}/confirm-payment")
    @ApiMessage("Xác nhận hoàn thành thành công")
    public ResponseEntity<Void> confirmPayment(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        jobService.confirmPayment(jobId, currentUser.getUserEntity().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{jobId}/reviews")
    @ApiMessage("Đánh giá thợ thành công")
    public ResponseEntity<Void> reviewHelper(
            @PathVariable Long jobId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        jobService.reviewHelper(jobId, currentUser.getUserEntity().getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{jobId}/evidence")
    @ApiMessage("Lấy danh sách ảnh bằng chứng thành công")
    public ResponseEntity<List<JobImageResponse>> getJobEvidence(
            @PathVariable Long jobId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        return ResponseEntity.ok(jobService.getJobEvidence(jobId, currentUser.getUserEntity().getId()));
    }

    @GetMapping("/{jobId}/review")
    @ApiMessage("Lấy thông tin đánh giá thành công")
    public ResponseEntity<ReviewResponse> getJobReview(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJobReview(jobId));
    }
}
