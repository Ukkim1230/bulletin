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
                // 모든 경로 허용 (로그인 기능 비활성화)
                .anyRequest().permitAll()
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
            // 로그인/로그아웃 기능 비활성화
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }
}
