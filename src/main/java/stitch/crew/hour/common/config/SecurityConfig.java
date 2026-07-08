package stitch.crew.hour.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import stitch.crew.hour.auth.service.OAuth2LoginSuccessHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAccessDeniedHandler;
import stitch.crew.hour.common.config.entrypoint.JwtAuthenticationEntryPoint;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .oauth2Login(oauth2 -> oauth2
                    .successHandler(oAuth2LoginSuccessHandler)
                )
                .exceptionHandling(exp -> exp
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth ->auth
                    .requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                    .requestMatchers("/api/users/me", "/api/users/me/**").authenticated()
                    .requestMatchers("/api/addresses", "/api/addresses/**").authenticated()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/admin/categories/**").hasRole("ADMIN")
                    .requestMatchers("/api/categories").hasAnyRole("ADMIN", "USER")
                    .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
