package com.church.bulletin.service;

import com.church.bulletin.entity.User;
import com.church.bulletin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 회원가입
     */
    public User registerUser(String username, String email, String password) {
        // 중복 검사
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("이미 사용 중인 사용자명입니다.");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        
        // 사용자 생성
        User user = User.builder()
                .username(username)
                .email(email)
                .password(encodedPassword)
                .role(User.UserRole.USER)
                .enabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("새 사용자 등록: {}", username);
        
        return savedUser;
    }
    
    /**
     * 사용자명으로 사용자 찾기
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 이메일로 사용자 찾기
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 로그인 시간 업데이트
     */
    public void updateLastLoginTime(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    userRepository.save(user);
                });
    }
    
    /**
     * 비밀번호 검증
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
