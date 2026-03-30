package vn.localhelp.core.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.domain.entity.Category;
import vn.localhelp.core.domain.entity.Job;
import vn.localhelp.core.domain.entity.JobImage;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.domain.mapper.JobMapper;
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
    private final JobMapper jobMapper;

    @PostMapping
  @ApiMessage("Create a new job successfully")
  public ResponseEntity<JobResponse> createJob(@RequestBody CreateJobRequest createJobRequest){
    String currentFirebaseUid = FirebaseUtil.getCurrentUserUid();
    return ResponseEntity.ok(jobService.createJob(currentFirebaseUid, createJobRequest));
  }

  @ApiMessage("Fetch open jobs successfully")
  @GetMapping
  public ResponseEntity<ResultPaginationDTO<List<JobResponse>>> getOpenJobs(
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
  @GetMapping("/search")
  @ApiMessage("search ")
  public ResponseEntity<List<JobResponse>> searchJobByKeyword(
          @RequestParam String keyword
  ){
      // 👤 Mock User
      User user1 = new User();
      user1.setId(1L);

      User user2 = new User();
      user2.setId(2L);

      // 📂 Mock Category
      Category category = new Category();
      category.setId(1L);
      category.setName("Sửa chữa");

      // 🖼️ Mock Images
      JobImage img1 = new JobImage();
      img1.setImageUrl("img1.png");

      JobImage img2 = new JobImage();
      img2.setImageUrl("img2.png");

      // 🧱 Mock Job
      Job job1 = new Job();
      job1.setId(1L);
      job1.setTitle("Sửa ống nước");
      job1.setDescription("Sửa chữa ống nước bị rò rỉ");
      job1.setPrice(150000.0);
      job1.setAddress("Hà Nội");
      job1.setLatitude(21.0285);
      job1.setLongitude(105.8542);
      job1.setJobStatus(JobStatus.OPEN);
      job1.setCreatedAt(LocalDateTime.now());
      job1.setCreator(user1);
      job1.setCategory(category);
      job1.setJobImages(List.of(img1, img2));

      Job job2 = new Job();
      job2.setId(2L);
      job2.setTitle("Dọn dẹp nhà");
      job2.setDescription("Dọn dẹp căn hộ");
      job2.setPrice(200000.0);
      job2.setAddress("HCM");
      job2.setLatitude(10.8231);
      job2.setLongitude(106.6297);
      job2.setJobStatus(JobStatus.OPEN);
      job2.setCreatedAt(LocalDateTime.now());
      job2.setCreator(user2);
      job2.setCategory(category);
      job2.setJobImages(List.of(img1));

      List<Job> jobs = List.of(job1, job2);

      return ResponseEntity.ok(jobs.stream().map(jobMapper::toResponse).collect(Collectors.toList()));
  }
}
