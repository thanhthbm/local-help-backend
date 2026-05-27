package vn.localhelp.core.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.credential.path}")
  private String credentialPath;

  /**
   * Khởi tạo Firebase Admin SDK từ file service account JSON.
   *
   * <p>Firebase Admin SDK được backend dùng để xác thực Firebase token và cập nhật mật khẩu
   * trong chức năng khôi phục mật khẩu.</p>
   *
   * @throws IOException nếu không đọc được file credential
   */
  @PostConstruct
  public void init() throws IOException {
    if (credentialPath == null || credentialPath.trim().isEmpty()) {
      throw new IllegalArgumentException("Đường dẫn file Firebase JSON bị null hoặc trống!");
    }

    if (FirebaseApp.getApps().isEmpty()) {
      try (FileInputStream serviceAccount = new FileInputStream(credentialPath)) {
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();
        FirebaseApp.initializeApp(options);
      }
    }
  }

  /**
   * Cung cấp Firestore bean cho các service cần đồng bộ trạng thái realtime.
   *
   * @return Firestore client lấy từ Firebase Admin SDK
   */
  @Bean
  public Firestore firestore() {
    return FirestoreClient.getFirestore();
  }
}
