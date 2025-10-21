package com.church.bulletin.controller.web;

import com.church.bulletin.entity.User;
import com.church.bulletin.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }
    
    /**
     * 회원가입 페이지
     */
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }
    
    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "사용자명 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/login";
            }
            
            User user = userOpt.get();
            
            if (!userService.checkPassword(password, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "사용자명 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/login";
            }
            
            if (!user.isEnabled()) {
                redirectAttributes.addFlashAttribute("error", "비활성화된 계정입니다.");
                return "redirect:/login";
            }
            
            // 세션에 사용자 정보 저장
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userRole", user.getRole().name());
            
            // 로그인 시간 업데이트
            userService.updateLastLoginTime(username);
            
            log.info("사용자 로그인: {}", username);
            redirectAttributes.addFlashAttribute("success", "로그인되었습니다.");
            
            return "redirect:/";
            
        } catch (Exception e) {
            log.error("로그인 실패", e);
            redirectAttributes.addFlashAttribute("error", "로그인 중 오류가 발생했습니다.");
            return "redirect:/login";
        }
    }
    
    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            // 비밀번호 확인
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/register";
            }
            
            // 비밀번호 길이 검증
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "비밀번호는 6자 이상이어야 합니다.");
                return "redirect:/register";
            }
            
            // 회원가입 처리
            User user = userService.registerUser(username, email, password);
            
            log.info("새 사용자 등록: {}", username);
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
            
            return "redirect:/login";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            log.error("회원가입 실패", e);
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/register";
        }
    }
    
    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("username");
        
        session.invalidate();
        
        if (username != null) {
            log.info("사용자 로그아웃: {}", username);
        }
        
        redirectAttributes.addFlashAttribute("success", "로그아웃되었습니다.");
        return "redirect:/";
    }
}
