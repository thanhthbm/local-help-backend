package vn.localhelp.core.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * Service tương tác với Firebase Admin SDK và Firestore từ phía Backend.
 *
 * <p>Hai chức năng chính:</p>
 * <ul>
 *   <li>setUserRole() – Gán custom claims (role) lên Firebase Auth cho user.</li>
 *   <li>updateJobStatusRealtime() – Cập nhật trạng thái job lên Firestore
 *       để Android nhận callback realtime khi job thay đổi trạng thái.</li>
 * </ul>
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

  private final Firestore firestore;

  public void setUserRole(String uid, String role) throws FirebaseAuthException {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);

    FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
  }

  /**
   * Cập nhật trạng thái công việc lên Firestore để Android nhận callback realtime.
   *
   * <p>Ghi vào collection 'job_updates', document jobId.toString().
   * Dùng SetOptions.merge() để chỉ cập nhật fields 'status' và 'updatedAt',
   * không xóa các fields khác đã có trong document.</p>
   *
   * <p>Guard null/blank: nếu jobId hoặc status không hợp lệ, bỏ qua và log warn
   * thay vì ném exception để không ảnh hưởng luồng nghiệp vụ chính.</p>
   *
   * @param jobId   ID của công việc cần cập nhật trạng thái
   * @param status  Trạng thái mới (vd: "COMPLETED", "CANCELLED")
   */
  public void updateJobStatusRealtime(Long jobId, String status) {
    if (jobId == null || status == null || status.isBlank()) {
      log.warn("Skip Firestore sync because jobId/status is invalid: jobId={}, status={}", jobId, status);
      return;
    }

    try {
      Map<String, Object> data = new HashMap<>();
      data.put("status", status);
      data.put("updatedAt", FieldValue.serverTimestamp());

      firestore.collection("job_updates")
          .document(jobId.toString())
          .set(data, SetOptions.merge())
          .get();

      log.info("Synced Job {} status {} to Firestore", jobId, status);
    } catch (Exception e) {
      log.error("Failed to sync status to Firestore for job {}: {}", jobId, e.getMessage(), e);
    }
  }
}
