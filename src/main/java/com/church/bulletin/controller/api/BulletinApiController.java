package com.church.bulletin.controller.api;

import com.church.bulletin.entity.*;
import com.church.bulletin.service.BulletinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bulletin")
@RequiredArgsConstructor
@Slf4j
public class BulletinApiController {
    
    private final BulletinService bulletinService;
    
    @GetMapping("/today")
    public ResponseEntity<BulletinService.BulletinData> getTodayBulletin() {
        log.info("오늘의 주보 정보 조회 요청");
        BulletinService.BulletinData bulletin = bulletinService.getTodayBulletin();
        return ResponseEntity.ok(bulletin);
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<BulletinService.BulletinData> getBulletinByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("특정 날짜 주보 정보 조회 요청 - 날짜: {}", date);
        BulletinService.BulletinData bulletin = bulletinService.getBulletinByDate(date);
        return ResponseEntity.ok(bulletin);
    }
    
    @GetMapping("/worship/recent")
    public ResponseEntity<List<WorshipService>> getRecentWorshipServices(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("최근 예배 정보 조회 요청 - 개수: {}", limit);
        List<WorshipService> services = bulletinService.getRecentWorshipServices(limit);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/worship/upcoming")
    public ResponseEntity<List<WorshipService>> getUpcomingWorshipServices(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("다가오는 예배 정보 조회 요청 - 개수: {}", limit);
        List<WorshipService> services = bulletinService.getUpcomingWorshipServices(limit);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getCurrentAnnouncements() {
        log.info("현재 공지사항 조회 요청");
        List<Announcement> announcements = bulletinService.getCurrentAnnouncements();
        return ResponseEntity.ok(announcements);
    }
    
    @GetMapping("/announcements/important")
    public ResponseEntity<List<Announcement>> getImportantAnnouncements() {
        log.info("중요 공지사항 조회 요청");
        List<Announcement> announcements = bulletinService.getImportantAnnouncements();
        return ResponseEntity.ok(announcements);
    }
    
    @GetMapping("/bible-verse/weekly")
    public ResponseEntity<BibleVerse> getWeeklyBibleVerse(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("주간 성경구절 조회 요청 - 날짜: {}", date);
        Optional<BibleVerse> verse = bulletinService.getWeeklyBibleVerse(date);
        return verse.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/prayer-requests")
    public ResponseEntity<List<PrayerRequest>> getPrayerRequests(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("기도제목 조회 요청 - 날짜: {}", date);
        List<PrayerRequest> prayerRequests = bulletinService.getPublicPrayerRequests(date);
        return ResponseEntity.ok(prayerRequests);
    }
    
    @GetMapping("/events/upcoming")
    public ResponseEntity<List<ChurchEvent>> getUpcomingEvents(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("다가오는 행사 조회 요청 - 개수: {}", limit);
        List<ChurchEvent> events = bulletinService.getUpcomingEvents(limit);
        return ResponseEntity.ok(events);
    }
}
