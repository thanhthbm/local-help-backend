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

  @Bean
  public Firestore firestore() {
    return FirestoreClient.getFirestore();
  }
}
