package vn.localhelp.core.util;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class FirebaseUtil {
  public static String getCurrentUserUid() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Kiểm tra kỹ các trường hợp không hợp lệ:
    // 1. authentication là null (Chưa qua filter bảo mật)
    // 2. !isAuthenticated (Chưa xác thực)
    // 3. Là AnonymousAuthenticationToken (Spring mặc định gán cho user chưa login ở các API public)
    // 4. Principal là "anonymousUser" (Chuỗi mặc định)
    if (authentication == null ||
        !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken ||
        "anonymousUser".equals(authentication.getPrincipal())) {

      // Throw lỗi 401 ngay lập tức
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    }

    return authentication.getName(); // Đây chính là UID từ FirebaseTokenFilter nạp vào
  }
}
