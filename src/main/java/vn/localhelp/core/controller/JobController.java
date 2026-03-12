package vn.localhelp.core.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.response.common.ResultPaginationDTO;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.service.JobService;
import vn.localhelp.core.util.FirebaseUtil;
import vn.localhelp.core.util.annotation.ApiMessage;
import vn.localhelp.core.util.constant.JobStatus;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;

  @PostMapping
  @ApiMessage("Create a new job successfully")
  public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest){
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.createJob(currentFirebaseUid, createJobRequest));
  }

  @ApiMessage("Fetch open jobs successfully")
  @GetMapping
  public ResponseEntity<ResultPaginationDTO> getOpenJobs(
      @RequestParam(defaultValue = "1") int current,
      @RequestParam(defaultValue = "10") int pageSize
  ){
    return ResponseEntity.ok(jobService.getOpenJob(current, pageSize));
  }

  @GetMapping("/my-jobs")
  @ApiMessage("Fetch my jobs successfully")
  public ResponseEntity<List<JobResponse>> getMyJobs(
      @RequestParam(required = false) JobStatus status
  ){
    List<JobResponse> jobs = jobService.getMyJobs(status);
    return ResponseEntity.ok(jobs);
  }
}
