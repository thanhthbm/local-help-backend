package vn.localhelp.core.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.service.JobService;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.constant.JobStatus;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;

  @PostMapping
  public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(jobService.createJob(currentFirebaseUid, createJobRequest));
  }

  @GetMapping
  public ResponseEntity<List<JobResponse>> getJobs(
      @RequestParam(required = false) JobStatus status,
      @RequestParam(required = false) Long categoryId) {
    return ResponseEntity.ok(jobService.getAllJobs(status, categoryId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
    return ResponseEntity.ok(jobService.getJobById(id));
  }

  @PatchMapping("/{id}/accept")
  public ResponseEntity<JobResponse> acceptJob(@PathVariable Long id) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.acceptJob(id, currentFirebaseUid));
  }

  @PatchMapping("/{id}/complete")
  public ResponseEntity<JobResponse> completeJob(@PathVariable Long id) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.completeJob(id, currentFirebaseUid));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<JobResponse> cancelJob(@PathVariable Long id) {
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.cancelJob(id, currentFirebaseUid));
  }
}
