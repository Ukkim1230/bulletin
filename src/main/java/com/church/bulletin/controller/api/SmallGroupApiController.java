package com.church.bulletin.controller.api;

import com.church.bulletin.entity.SmallGroup;
import com.church.bulletin.service.SmallGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/small-groups")
@RequiredArgsConstructor
@Slf4j
public class SmallGroupApiController {
    
    private final SmallGroupService smallGroupService;
    
    /**
     * 모든 순모임 조회
     */
    @GetMapping
    public ResponseEntity<List<SmallGroup>> getAllSmallGroups() {
        try {
            List<SmallGroup> smallGroups = smallGroupService.getAllActiveSmallGroups();
            log.info("순모임 조회 성공: {}개", smallGroups.size());
            return ResponseEntity.ok(smallGroups);
        } catch (Exception e) {
            log.error("순모임 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 순모임 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<SmallGroup> getSmallGroup(@PathVariable Long id) {
        try {
            SmallGroup smallGroup = smallGroupService.getSmallGroupById(id);
            if (smallGroup == null) {
                return ResponseEntity.notFound().build();
            }
            log.info("순모임 상세 조회 성공: {}", id);
            return ResponseEntity.ok(smallGroup);
        } catch (Exception e) {
            log.error("순모임 상세 조회 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 순모임 생성 (JSON)
     */
    @PostMapping
    public ResponseEntity<SmallGroup> createSmallGroup(@RequestBody SmallGroup smallGroup) {
        try {
            log.info("순모임 생성 요청: name={}, leader={}, category={}", 
                    smallGroup.getName(), smallGroup.getLeader(), smallGroup.getCategory());
            
            SmallGroup savedSmallGroup = smallGroupService.createSmallGroup(smallGroup);
            log.info("순모임 생성 성공: {}", savedSmallGroup.getId());
            return ResponseEntity.ok(savedSmallGroup);
        } catch (Exception e) {
            log.error("순모임 생성 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 순모임 생성 (파일 업로드 포함)
     */
    @PostMapping("/with-files")
    public ResponseEntity<SmallGroup> createSmallGroupWithFiles(@ModelAttribute SmallGroup smallGroup) {
        try {
            log.info("순모임 생성 요청 (파일 포함): name={}, leader={}, category={}", 
                    smallGroup.getName(), smallGroup.getLeader(), smallGroup.getCategory());
            
            SmallGroup savedSmallGroup = smallGroupService.createSmallGroup(smallGroup);
            log.info("순모임 생성 성공: {}", savedSmallGroup.getId());
            return ResponseEntity.ok(savedSmallGroup);
        } catch (Exception e) {
            log.error("순모임 생성 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 순모임 수정 (JSON)
     */
    @PutMapping("/{id}")
    public ResponseEntity<SmallGroup> updateSmallGroup(@PathVariable Long id, @RequestBody SmallGroup smallGroup) {
        try {
            smallGroup.setId(id);
            SmallGroup updatedSmallGroup = smallGroupService.updateSmallGroup(smallGroup);
            if (updatedSmallGroup == null) {
                return ResponseEntity.notFound().build();
            }
            log.info("순모임 수정 성공: {}", id);
            return ResponseEntity.ok(updatedSmallGroup);
        } catch (Exception e) {
            log.error("순모임 수정 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 순모임 수정 (파일 업로드 포함)
     */
    @PutMapping("/{id}/with-files")
    public ResponseEntity<SmallGroup> updateSmallGroupWithFiles(@PathVariable Long id, @ModelAttribute SmallGroup smallGroup) {
        try {
            smallGroup.setId(id);
            SmallGroup updatedSmallGroup = smallGroupService.updateSmallGroup(smallGroup);
            if (updatedSmallGroup == null) {
                return ResponseEntity.notFound().build();
            }
            log.info("순모임 수정 성공: {}", id);
            return ResponseEntity.ok(updatedSmallGroup);
        } catch (Exception e) {
            log.error("순모임 수정 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 순모임 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSmallGroup(@PathVariable Long id) {
        try {
            boolean deleted = smallGroupService.deleteSmallGroup(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            log.info("순모임 삭제 성공: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("순모임 삭제 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 카테고리별 순모임 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SmallGroup>> getSmallGroupsByCategory(@PathVariable String category) {
        try {
            List<SmallGroup> smallGroups = smallGroupService.getSmallGroupsByCategory(category);
            log.info("카테고리별 순모임 조회 성공: {}개", smallGroups.size());
            return ResponseEntity.ok(smallGroups);
        } catch (Exception e) {
            log.error("카테고리별 순모임 조회 실패: {}", category, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 순모임 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<SmallGroup>> searchSmallGroups(@RequestParam String keyword) {
        try {
            List<SmallGroup> smallGroups = smallGroupService.searchSmallGroups(keyword);
            log.info("순모임 검색 성공: {}개", smallGroups.size());
            return ResponseEntity.ok(smallGroups);
        } catch (Exception e) {
            log.error("순모임 검색 실패: {}", keyword, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 순모임 가입 신청
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinSmallGroup(@PathVariable Long id, @RequestBody JoinRequest joinRequest) {
        try {
            boolean success = smallGroupService.joinSmallGroup(id, joinRequest);
            if (success) {
                log.info("순모임 가입 신청 성공: {}", id);
                return ResponseEntity.ok("가입 신청이 완료되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("가입 신청에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("순모임 가입 신청 실패: {}", id, e);
            return ResponseEntity.internalServerError().body("가입 신청 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 순모임 가입 신청 DTO
     */
    public static class JoinRequest {
        private String name;
        private String phone;
        private String email;
        private String message;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
