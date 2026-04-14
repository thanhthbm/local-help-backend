package vn.localhelp.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.localhelp.core.repository.UserRepository;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final UserRepository userRepository;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    return http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .exceptionHandling(
            ex -> ex
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/icons/**", "/images/**").permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/users/me").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(
            new FirebaseAuthFilter(userRepository),
            UsernamePasswordAuthenticationFilter.class
        )
        .build();
  }
}
