package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.request.job.CreateJobRequest;
import vn.localhelp.core.domain.response.job.JobResponse;
import vn.localhelp.core.service.JobService;
import vn.localhelp.core.util.FirebaseUtil;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;

  @PostMapping
  public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest){
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.createJob(currentFirebaseUid, createJobRequest));
  }
}
