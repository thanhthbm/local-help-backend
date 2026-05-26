package vn.localhelp.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.localhelp.core.service.ForgotPasswordService;
import vn.localhelp.core.util.annotation.ApiMessage;

import java.util.Map;

/**
 * REST Controller xử lý luồng khôi phục mật khẩu bằng email OTP.
 *
 * <p>Luồng gồm 3 bước:</p>
 * <ol>
 *   <li>POST /send-otp: gửi mã OTP đến email người dùng.</li>
 *   <li>POST /verify-otp: xác thực OTP và trả resetToken.</li>
 *   <li>POST /reset-password: dùng resetToken để đặt mật khẩu mới trên Firebase Authentication.</li>
 * </ol>
 */
@RestController
@RequestMapping("/api/auth/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    /**
     * Gửi mã OTP khôi phục mật khẩu đến email người dùng.
     *
     * @param email Email tài khoản cần khôi phục mật khẩu
     * @return      HTTP 200 nếu OTP được gửi thành công
     */
    @PostMapping("/send-otp")
    @ApiMessage("Gửi mã OTP thành công")
    public ResponseEntity<Void> sendOtp(@RequestParam String email) {
        forgotPasswordService.sendOtp(email);
        return ResponseEntity.ok().build();
    }

    /**
     * Xác thực mã OTP người dùng nhập.
     *
     * <p>Nếu OTP hợp lệ, backend trả về resetToken để Android dùng ở bước đặt lại mật khẩu.</p>
     *
     * @param email Email đã nhận OTP
     * @param otp   Mã OTP người dùng nhập
     * @return      Map chứa resetToken
     */
    @PostMapping("/verify-otp")
    @ApiMessage("Xác thực OTP thành công")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        String resetToken = forgotPasswordService.verifyOtp(email, otp);
        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }

    /**
     * Đặt lại mật khẩu mới sau khi OTP đã được xác thực.
     *
     * @param email       Email tài khoản cần đổi mật khẩu
     * @param resetToken  Token được cấp sau bước verify OTP
     * @param newPassword Mật khẩu mới cần cập nhật lên Firebase Authentication
     * @return            HTTP 200 nếu cập nhật mật khẩu thành công
     */
    @PostMapping("/reset-password")
    @ApiMessage("Đặt lại mật khẩu thành công")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String email,
            @RequestParam String resetToken,
            @RequestParam String newPassword) {
        forgotPasswordService.resetPassword(email, resetToken, newPassword);
        return ResponseEntity.ok().build();
    }
}
