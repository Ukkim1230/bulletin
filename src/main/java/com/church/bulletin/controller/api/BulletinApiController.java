package com.church.bulletin.controller.api;

import com.church.bulletin.entity.*;
import com.church.bulletin.service.BulletinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "주보 API", description = "교회 주보 관련 API")
public class BulletinApiController {
    
    private final BulletinService bulletinService;
    
    @GetMapping("/today")
    @Operation(summary = "오늘의 주보 조회", description = "오늘 날짜의 모든 주보 정보를 조회합니다")
    public ResponseEntity<BulletinService.BulletinData> getTodayBulletin() {
        log.info("오늘의 주보 정보 조회 요청");
        BulletinService.BulletinData bulletin = bulletinService.getTodayBulletin();
        return ResponseEntity.ok(bulletin);
    }
    
    @GetMapping("/date/{date}")
    @Operation(summary = "특정 날짜 주보 조회", description = "지정된 날짜의 모든 주보 정보를 조회합니다")
    public ResponseEntity<BulletinService.BulletinData> getBulletinByDate(
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2024-09-22")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("특정 날짜 주보 정보 조회 요청 - 날짜: {}", date);
        BulletinService.BulletinData bulletin = bulletinService.getBulletinByDate(date);
        return ResponseEntity.ok(bulletin);
    }
    
    @GetMapping("/worship/recent")
    @Operation(summary = "최근 예배 정보 조회", description = "최근 예배 정보를 조회합니다")
    public ResponseEntity<List<WorshipService>> getRecentWorshipServices(
            @Parameter(description = "조회할 개수", example = "5")
            @RequestParam(defaultValue = "5") int limit) {
        log.info("최근 예배 정보 조회 요청 - 개수: {}", limit);
        List<WorshipService> services = bulletinService.getRecentWorshipServices(limit);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/worship/upcoming")
    @Operation(summary = "다가오는 예배 정보 조회", description = "다가오는 예배 정보를 조회합니다")
    public ResponseEntity<List<WorshipService>> getUpcomingWorshipServices(
            @Parameter(description = "조회할 개수", example = "5")
            @RequestParam(defaultValue = "5") int limit) {
        log.info("다가오는 예배 정보 조회 요청 - 개수: {}", limit);
        List<WorshipService> services = bulletinService.getUpcomingWorshipServices(limit);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/announcements")
    @Operation(summary = "현재 공지사항 조회", description = "현재 활성화된 모든 공지사항을 조회합니다")
    public ResponseEntity<List<Announcement>> getCurrentAnnouncements() {
        log.info("현재 공지사항 조회 요청");
        List<Announcement> announcements = bulletinService.getCurrentAnnouncements();
        return ResponseEntity.ok(announcements);
    }
    
    @GetMapping("/announcements/important")
    @Operation(summary = "중요 공지사항 조회", description = "중요 공지사항만을 조회합니다")
    public ResponseEntity<List<Announcement>> getImportantAnnouncements() {
        log.info("중요 공지사항 조회 요청");
        List<Announcement> announcements = bulletinService.getImportantAnnouncements();
        return ResponseEntity.ok(announcements);
    }
    
    @GetMapping("/bible-verse/weekly")
    @Operation(summary = "주간 성경구절 조회", description = "지정된 날짜의 주간 성경구절을 조회합니다")
    public ResponseEntity<BibleVerse> getWeeklyBibleVerse(
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2024-09-22")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("주간 성경구절 조회 요청 - 날짜: {}", date);
        Optional<BibleVerse> verse = bulletinService.getWeeklyBibleVerse(date);
        return verse.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/prayer-requests")
    @Operation(summary = "기도제목 조회", description = "지정된 날짜의 공개 기도제목을 조회합니다")
    public ResponseEntity<List<PrayerRequest>> getPrayerRequests(
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2024-09-22")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("기도제목 조회 요청 - 날짜: {}", date);
        List<PrayerRequest> prayerRequests = bulletinService.getPublicPrayerRequests(date);
        return ResponseEntity.ok(prayerRequests);
    }
    
    @GetMapping("/events/upcoming")
    @Operation(summary = "다가오는 행사 조회", description = "다가오는 교회 행사를 조회합니다")
    public ResponseEntity<List<ChurchEvent>> getUpcomingEvents(
            @Parameter(description = "조회할 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("다가오는 행사 조회 요청 - 개수: {}", limit);
        List<ChurchEvent> events = bulletinService.getUpcomingEvents(limit);
        return ResponseEntity.ok(events);
    }
}
