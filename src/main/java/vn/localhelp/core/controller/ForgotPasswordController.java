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

@RestController
@RequestMapping("/api/auth/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    // Bước 1 đổi mật khẩu: nhận email từ Android và yêu cầu service gửi OTP.
    @PostMapping("/send-otp")
    @ApiMessage("Gửi mã OTP thành công")
    public ResponseEntity<Void> sendOtp(@RequestParam String email) {
        forgotPasswordService.sendOtp(email);
        return ResponseEntity.ok().build();
    }

    // Bước 2 đổi mật khẩu: xác thực OTP và trả resetToken cho Android.
    @PostMapping("/verify-otp")
    @ApiMessage("Xác thực OTP thành công")
    public ResponseEntity<Map<String, String>> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        String resetToken = forgotPasswordService.verifyOtp(email, otp);
        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }

    // Bước 3 đổi mật khẩu: nhận resetToken và mật khẩu mới để cập nhật trên Firebase.
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
