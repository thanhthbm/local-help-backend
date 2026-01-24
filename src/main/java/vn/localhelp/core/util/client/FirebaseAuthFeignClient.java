package vn.localhelp.core.util.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import vn.localhelp.core.domain.request.auth.FirebaseLoginRequest;
import vn.localhelp.core.domain.response.auth.FirebaseLoginResponse;

@FeignClient(
    name = "firebaseAuthClient",
    url = "${firebase.auth.url}"
)
public interface FirebaseAuthFeignClient {
  @PostMapping(
      value = "v1/accounts:signInWithPassword",
      consumes = "application/json"
  )
  FirebaseLoginResponse login(@RequestParam("key") String apiKey, @RequestBody FirebaseLoginRequest request);
}
