package com.church.bulletin.service;

import com.church.bulletin.entity.*;
import com.church.bulletin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BulletinService {
    
    private final WorshipServiceRepository worshipServiceRepository;
    private final AnnouncementRepository announcementRepository;
    private final BibleVerseRepository bibleVerseRepository;
    private final PrayerRequestRepository prayerRequestRepository;
    private final ChurchEventRepository churchEventRepository;
    
    /**
     * 특정 날짜의 주보 정보를 모두 조회
     */
    public BulletinData getBulletinByDate(LocalDate date) {
        log.info("주보 정보 조회 - 날짜: {}", date);
        
        return BulletinData.builder()
                .worshipServices(worshipServiceRepository.findByServiceDateOrderByServiceTime(date))
                .announcements(announcementRepository.findCurrentActiveAnnouncements(date))
                .bibleVerses(bibleVerseRepository.findByServiceDate(date))
                .prayerRequests(prayerRequestRepository.findByRequestDateAndIsPublicTrueOrderByCreatedAtDesc(date))
                .upcomingEvents(churchEventRepository.findUpcomingEvents(date))
                .build();
    }
    
    /**
     * 오늘의 주보 정보 조회
     */
    public BulletinData getTodayBulletin() {
        return getBulletinByDate(LocalDate.now());
    }
    
    /**
     * 최근 예배 정보 조회
     */
    public List<WorshipService> getRecentWorshipServices(int limit) {
        List<WorshipService> services = worshipServiceRepository.findRecentServices(LocalDate.now());
        return services.stream().limit(limit).toList();
    }
    
    /**
     * 다가오는 예배 정보 조회
     */
    public List<WorshipService> getUpcomingWorshipServices(int limit) {
        List<WorshipService> services = worshipServiceRepository.findUpcomingServices(LocalDate.now());
        return services.stream().limit(limit).toList();
    }
    
    /**
     * 중요 공지사항 조회
     */
    public List<Announcement> getImportantAnnouncements() {
        return announcementRepository.findByIsActiveTrueAndIsImportantTrueOrderByDisplayOrderAscCreatedAtDesc();
    }
    
    /**
     * 현재 활성화된 모든 공지사항 조회
     */
    public List<Announcement> getCurrentAnnouncements() {
        return announcementRepository.findCurrentActiveAnnouncements(LocalDate.now());
    }
    
    /**
     * 주간 성경구절 조회
     */
    public Optional<BibleVerse> getWeeklyBibleVerse(LocalDate date) {
        return bibleVerseRepository.findByServiceDateAndVerseType(date, BibleVerse.VerseType.WEEKLY);
    }
    
    /**
     * 공개 기도제목 조회
     */
    public List<PrayerRequest> getPublicPrayerRequests(LocalDate date) {
        return prayerRequestRepository.findByRequestDateAndIsPublicTrueOrderByCreatedAtDesc(date);
    }
    
    /**
     * 다가오는 교회 행사 조회
     */
    public List<ChurchEvent> getUpcomingEvents(int limit) {
        List<ChurchEvent> events = churchEventRepository.findUpcomingEvents(LocalDate.now());
        return events.stream().limit(limit).toList();
    }
    
    // 주보 데이터 DTO
    @lombok.Data
    @lombok.Builder
    public static class BulletinData {
        private List<WorshipService> worshipServices;
        private List<Announcement> announcements;
        private List<BibleVerse> bibleVerses;
        private List<PrayerRequest> prayerRequests;
        private List<ChurchEvent> upcomingEvents;
    }
}
