package com.church.bulletin.controller.api;

import com.church.bulletin.entity.Announcement;
import com.church.bulletin.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Slf4j
public class AnnouncementApiController {
    
    private final AnnouncementService announcementService;
    
    /**
     * 모든 공지사항 조회
     */
    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getAllActiveAnnouncements();
            log.info("공지사항 조회 성공: {}개", announcements.size());
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            log.error("공지사항 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (announcement == null) {
                return ResponseEntity.notFound().build();
            }
            log.info("공지사항 상세 조회 성공: {}", id);
            return ResponseEntity.ok(announcement);
        } catch (Exception e) {
            log.error("공지사항 상세 조회 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 공지사항 생성
     */
    @PostMapping
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        try {
            log.info("공지사항 생성 요청: title={}, content={}, date={}", 
                    announcement.getTitle(), announcement.getContent(), announcement.getAnnouncementDate());
            
            Announcement savedAnnouncement = announcementService.createAnnouncement(announcement);
            log.info("공지사항 생성 성공: {}", savedAnnouncement.getId());
            return ResponseEntity.ok(savedAnnouncement);
        } catch (Exception e) {
            log.error("공지사항 생성 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 공지사항 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Announcement> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        try {
            announcement.setId(id);
            Announcement updatedAnnouncement = announcementService.updateAnnouncement(announcement);
            if (updatedAnnouncement == null) {
                return ResponseEntity.notFound().build();
            }
            log.info("공지사항 수정 성공: {}", id);
            return ResponseEntity.ok(updatedAnnouncement);
        } catch (Exception e) {
            log.error("공지사항 수정 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            boolean deleted = announcementService.deleteAnnouncement(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            log.info("공지사항 삭제 성공: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("공지사항 삭제 실패: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
