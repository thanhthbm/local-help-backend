package vn.localhelp.core.util;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class FirebaseUtil {
  /**
   * Lấy Firebase UID của user hiện tại từ SecurityContext.
   *
   * <p>Hàm kiểm tra các trường hợp chưa đăng nhập như authentication null, anonymous user
   * hoặc principal không hợp lệ. Nếu request đã được FirebaseAuthFilter xử lý, UID được lấy
   * từ CustomUserDetails hoặc authentication name.</p>
   *
   * @return Firebase UID của user hiện tại
   * @throws ResponseStatusException HTTP 401 nếu request chưa được xác thực
   */
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

      Object principal = authentication.getPrincipal();

      if (principal instanceof CustomUserDetails) {
          return ((CustomUserDetails) principal).getFirebaseUid();
      }

    return authentication.getName(); // Đây chính là UID từ FirebaseTokenFilter nạp vào
  }
}
