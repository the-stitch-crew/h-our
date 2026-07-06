package stitch.crew.hour.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .authorizeHttpRequests(auth ->auth
                    .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/users/me", "/api/users/me/password").authenticated()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
