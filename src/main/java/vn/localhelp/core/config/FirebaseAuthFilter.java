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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      try{
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);


        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = (String) decodedToken.getClaims().get("role");

        if (role != null) {
          authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        } else {
          authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

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
