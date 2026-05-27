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

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

  private final Firestore firestore;

  /**
   * Gán custom claim role cho Firebase user.
   *
   * Đây là API ngoài của Firebase Admin SDK; backend có thể dùng claim này để
   * đồng bộ quyền giữa Firebase và bảng users nếu cần.
   */
  public void setUserRole(String uid, String role) throws FirebaseAuthException {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);

    FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
  }

  /**
   * Cập nhật status của Job lên Firestore để app nhận callback real-time
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
