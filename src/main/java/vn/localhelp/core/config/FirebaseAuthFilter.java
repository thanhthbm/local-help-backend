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

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.localhelp.core.domain.entity.User;
import vn.localhelp.core.repository.UserRepository;
import vn.localhelp.core.util.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import vn.localhelp.core.util.constant.UserRole;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      log.info("Bearer Token: {}", token);

      try{
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);


        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        log.info("uid: {}, email: {}", uid, email);

        User user = userRepository.findByFirebaseUid(uid)
            .orElseGet(() -> User.builder().role(UserRole.USER).build());

        String role = user.getRole().name();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));


        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(uid, email, authorities);


        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
