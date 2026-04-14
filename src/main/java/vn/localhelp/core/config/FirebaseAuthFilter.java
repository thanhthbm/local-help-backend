package vn.localhelp.core.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import vn.localhelp.core.util.CustomUserDetails;
import vn.localhelp.core.util.constant.UserRole;

@Slf4j
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (header != null && header.toLowerCase().startsWith("bearer ")) {
      String token = header.substring(7);
      log.info("Auth Token found for {} {}", request.getMethod(), request.getRequestURI());

      try {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        
        log.debug("Authenticated UID: {}", uid);

        User user = userRepository.findByFirebaseUid(uid)
            .orElseGet(() -> {
              log.warn("User with UID {} not found in database, creating temporary principal", uid);
              User newUser = new User();
              newUser.setFirebaseUid(uid);
              newUser.setEmail(email);
              newUser.setRole(UserRole.USER); // Ensure role is NOT null
              return newUser;
            });

        String roleName = (user.getRole() != null) ? user.getRole().name() : "USER";
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

        CustomUserDetails principal = new CustomUserDetails(uid, user);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        log.error("Firebase token verification failed for {} {}: {}", 
            request.getMethod(), request.getRequestURI(), e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Bearer realm=\"localhelp\", error=\"invalid_token\"");
        response.getWriter().write("{\"message\": \"Invalid or expired token\"}");
        return;
      }
    } else {
      log.info("No Bearer token found for {} {}", request.getMethod(), request.getRequestURI());
    }

    filterChain.doFilter(request, response);
  }
}
