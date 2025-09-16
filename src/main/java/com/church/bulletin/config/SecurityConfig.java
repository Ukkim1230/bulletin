package com.church.bulletin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 공개 API 및 페이지
                .requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/mobile"),
                    new AntPathRequestMatcher("/bulletin/**"),
                    new AntPathRequestMatcher("/api/bulletin/**")
                ).permitAll()
                // 정적 리소스
                .requestMatchers(
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/favicon.ico")
                ).permitAll()
                // H2 콘솔 (개발용)
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                // Swagger UI
                .requestMatchers(
                    new AntPathRequestMatcher("/swagger-ui/**"),
                    new AntPathRequestMatcher("/api-docs/**"),
                    new AntPathRequestMatcher("/v3/api-docs/**")
                ).permitAll()
                // 관리자 페이지는 인증 필요
                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                // H2 콘솔을 위해 CSRF 비활성화
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                // API 엔드포인트에 대해 CSRF 비활성화
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**"))
            )
            .headers(headers -> headers
                // H2 콘솔을 위해 frame options 비활성화
                .frameOptions().sameOrigin()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
