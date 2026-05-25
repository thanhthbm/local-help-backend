package vn.localhelp.core.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import vn.localhelp.core.repository.UserRepository;
/**
 * Cấu hình WebSocket STOMP Broker cho hệ thống push notification realtime.
 *
 * <p>WebSocket được dùng để gửi thông báo từ server xuống Android (server-push),
 * ví dụ: thông báo có người ứng tuyển, job được chấp nhận, v.v.</p>
 *
 * <p><b>Lưu ý:</b> Tin nhắn chat KHÔNG qua WebSocket này – được xử lý trực tiếp
 * qua Firebase Firestore SDK trên Android. WebSocket chỉ dùng cho notification.</p>
 *
 * <p>@Order(HIGHEST_PRECEDENCE + 99): Đảm bảo filter này chạy trước Spring Security
 * để xác thực token Firebase tại thời điểm STOMP CONNECT.</p>
 *
 */

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final UserRepository userRepository;
  /**
   * Cấu hình in-memory message broker cho STOMP.
   *
   * <p>Broker prefixes: /topic (broadcast), /user (private to user).
   * Application prefix /app: tất cả message gửi đến server bắt đầu bằng /app.
   * User destination prefix /user: server gửi về đúng user với /user/{userId}/...</p>
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/user");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
  }
  /**
   * Đăng ký endpoint WebSocket: /ws-notifications.
   *
   * <p>withSockJS() cho phép fallback về HTTP Long-polling nếu WebSocket không khả dụng.
   * setAllowedOriginPatterns("*") cho phép tất cả origin – cần giới hạn trong production.</p>
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws-notifications").setAllowedOriginPatterns("*").withSockJS();
  }
  // Xác thực Firebase JWT token khi client gửi STOMP CONNECT frame.
  // Token được đọc từ STOMP header 'Authorization: Bearer <token>'.
  // Nếu hợp lệ, gán UsernamePasswordAuthenticationToken vào STOMP session,
  // cho phép server biết user nào đang kết nối WebSocket.
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
          String authToken = accessor.getFirstNativeHeader("Authorization");

          if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7);
            try {
              FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
              String uid = decodedToken.getUid();

              userRepository.findByFirebaseUid(uid).ifPresent(user -> {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority(user.getRole().name())));
                accessor.setUser(authentication);
                log.info("Authenticated WebSocket user: {}", user.getEmail());
              });

            } catch (Exception e) {
              log.error("WebSocket connection failed: Invalid Firebase token.", e);
            }
          }
        }
        return message;
      }
    });
  }
}
