package vn.localhelp.core.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.localhelp.core.util.error.NotFoundException;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service xử lý nghiệp vụ khôi phục mật khẩu bằng OTP email.
 *
 * <p>Service dùng Firebase Authentication để kiểm tra tài khoản và cập nhật mật khẩu.
 * OTP/resetToken hiện được lưu tạm bằng ConcurrentHashMap theo email; khi triển khai nhiều
 * instance nên chuyển sang Redis để các instance dùng chung trạng thái.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    private final JavaMailSender mailSender;
    
    /**
     * Bộ nhớ tạm lưu OTP/resetToken theo email trong luồng khôi phục mật khẩu.
     */
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, String> resetTokenStorage = new ConcurrentHashMap<>();
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Gửi OTP khôi phục mật khẩu đến email.
     *
     * <p>Service kiểm tra email có tồn tại trên Firebase Authentication, sinh OTP 6 chữ số,
     * lưu OTP trong 5 phút và gửi email qua JavaMailSender.</p>
     *
     * @param email Email tài khoản cần khôi phục mật khẩu
     * @throws NotFoundException nếu email không tồn tại trên Firebase
     * @throws RuntimeException  nếu hệ thống không gửi được email
     */
    public void sendOtp(String email) {
        try {
            // Validate if user exists on Firebase
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
            
            // Generate 6 digit OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            
            // Store OTP
            otpStorage.put(email, otp);
            
            // Schedule OTP expiry after 5 minutes
            scheduler.schedule(() -> otpStorage.remove(email), 5, TimeUnit.MINUTES);
            
            // Send Email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Mã OTP khôi phục mật khẩu - LocalHelp");
            message.setText("Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.");
            
            mailSender.send(message);
            log.info("OTP sent to email: {}", email);
            
        } catch (FirebaseAuthException e) {
            log.error("Email not found on Firebase: {}", email);
            throw new NotFoundException("Email không tồn tại trong hệ thống");
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email, vui lòng thử lại sau.");
        }
    }

    /**
     * Xác thực OTP và sinh resetToken.
     *
     * <p>OTP hợp lệ sẽ bị xóa ngay sau khi dùng để tránh sử dụng lại. resetToken được lưu
     * tạm trong 15 phút và là điều kiện bắt buộc ở bước đặt lại mật khẩu.</p>
     *
     * @param email Email đã nhận OTP
     * @param otp   Mã OTP người dùng nhập
     * @return      resetToken dùng cho API reset-password
     * @throws RuntimeException nếu OTP sai hoặc đã hết hạn
     */
    public String verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn");
        }
        
        // OTP is valid, generate reset token
        String resetToken = UUID.randomUUID().toString();
        resetTokenStorage.put(email, resetToken);
        
        // Remove OTP as it has been used
        otpStorage.remove(email);
        
        // Expire token after 15 minutes
        scheduler.schedule(() -> resetTokenStorage.remove(email), 15, TimeUnit.MINUTES);
        
        return resetToken;
    }

    /**
     * Đặt lại mật khẩu mới trên Firebase Authentication.
     *
     * <p>Service kiểm tra resetToken theo email, tìm user trên Firebase và cập nhật password
     * bằng Firebase Admin SDK. Token được xóa sau khi cập nhật thành công.</p>
     *
     * @param email       Email tài khoản cần đổi mật khẩu
     * @param resetToken  Token đã được cấp ở bước xác thực OTP
     * @param newPassword Mật khẩu mới
     * @throws RuntimeException nếu resetToken không hợp lệ hoặc Firebase cập nhật thất bại
     */
    public void resetPassword(String email, String resetToken, String newPassword) {
        String storedToken = resetTokenStorage.get(email);
        
        if (storedToken == null || !storedToken.equals(resetToken)) {
            throw new RuntimeException("Phiên làm việc không hợp lệ hoặc đã hết hạn");
        }
        
        try {
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);
            
            UpdateRequest request = new UpdateRequest(user.getUid())
                .setPassword(newPassword);
            
            FirebaseAuth.getInstance().updateUser(request);
            
            // Invalidate token after successful reset
            resetTokenStorage.remove(email);
            log.info("Password successfully reset for email: {}", email);
            
        } catch (FirebaseAuthException e) {
            log.error("Failed to update password in Firebase: {}", e.getMessage());
            throw new RuntimeException("Không thể cập nhật mật khẩu, vui lòng thử lại sau.");
        }
    }
}
