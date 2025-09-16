package com.church.bulletin.config;

import com.church.bulletin.entity.*;
import com.church.bulletin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final WorshipServiceRepository worshipServiceRepository;
    private final AnnouncementRepository announcementRepository;
    private final BibleVerseRepository bibleVerseRepository;
    private final PrayerRequestRepository prayerRequestRepository;
    private final ChurchEventRepository churchEventRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("샘플 데이터 초기화 시작");
        
        // 오늘 날짜 기준 샘플 데이터 생성
        LocalDate today = LocalDate.now();
        
        // 예배 정보 샘플 데이터
        if (worshipServiceRepository.count() == 0) {
            WorshipService sundayService = WorshipService.builder()
                    .serviceDate(today)
                    .serviceType("주일예배")
                    .serviceTime(LocalTime.of(11, 0))
                    .preacher("김목사")
                    .sermonTitle("하나님의 사랑")
                    .sermonText("요한복음 3:16")
                    .build();
            
            WorshipService eveningService = WorshipService.builder()
                    .serviceDate(today)
                    .serviceType("저녁예배")
                    .serviceTime(LocalTime.of(19, 0))
                    .preacher("이목사")
                    .sermonTitle("평안을 주시는 하나님")
                    .sermonText("요한복음 14:27")
                    .build();
            
            worshipServiceRepository.save(sundayService);
            worshipServiceRepository.save(eveningService);
            
            // 찬양 정보 추가
            PraiseSong song1 = PraiseSong.builder()
                    .worshipService(sundayService)
                    .songOrder(1)
                    .songType(PraiseSong.SongType.HYMN)
                    .songTitle("찬송하라 복되신 구세주 예수")
                    .songNumber("91")
                    .build();
            
            PraiseSong song2 = PraiseSong.builder()
                    .worshipService(sundayService)
                    .songOrder(2)
                    .songType(PraiseSong.SongType.CCM)
                    .songTitle("주님의 은혜")
                    .build();
            
            sundayService.addPraiseSong(song1);
            sundayService.addPraiseSong(song2);
            
            log.info("예배 정보 샘플 데이터 생성 완료");
        }
        
        // 공지사항 샘플 데이터
        if (announcementRepository.count() == 0) {
            Announcement announcement1 = Announcement.builder()
                    .title("추석 연휴 예배 안내")
                    .content("추석 연휴 기간(9/28-10/1) 예배 시간이 변경됩니다. 자세한 내용은 교회 홈페이지를 확인해주세요.")
                    .announcementDate(today)
                    .isImportant(true)
                    .displayOrder(1)
                    .build();
            
            Announcement announcement2 = Announcement.builder()
                    .title("새가족 환영식")
                    .content("이번 주일 예배 후 새가족 환영식이 있습니다. 많은 참석 부탁드립니다.")
                    .announcementDate(today)
                    .isImportant(false)
                    .displayOrder(2)
                    .build();
            
            announcementRepository.save(announcement1);
            announcementRepository.save(announcement2);
            
            log.info("공지사항 샘플 데이터 생성 완료");
        }
        
        // 성경구절 샘플 데이터
        if (bibleVerseRepository.count() == 0) {
            BibleVerse bibleVerse = BibleVerse.builder()
                    .serviceDate(today)
                    .verseReference("요한복음 3:16")
                    .verseText("하나님이 세상을 이처럼 사랑하사 독생자를 주셨으니 이는 그를 믿는 자마다 멸망하지 않고 영생을 얻게 하려 하심이라")
                    .verseType(BibleVerse.VerseType.WEEKLY)
                    .build();
            
            bibleVerseRepository.save(bibleVerse);
            
            log.info("성경구절 샘플 데이터 생성 완료");
        }
        
        // 기도제목 샘플 데이터
        if (prayerRequestRepository.count() == 0) {
            PrayerRequest prayer1 = PrayerRequest.builder()
                    .title("교회 부흥을 위한 기도")
                    .content("하나님의 은혜가 우리 교회에 충만하게 임하시도록")
                    .requestDate(today)
                    .category("교회")
                    .isPublic(true)
                    .build();
            
            PrayerRequest prayer2 = PrayerRequest.builder()
                    .title("선교사님들을 위한 기도")
                    .content("전 세계에서 사역하고 계신 선교사님들의 건강과 사역을 위해")
                    .requestDate(today)
                    .category("선교")
                    .isPublic(true)
                    .build();
            
            prayerRequestRepository.save(prayer1);
            prayerRequestRepository.save(prayer2);
            
            log.info("기도제목 샘플 데이터 생성 완료");
        }
        
        // 교회 행사 샘플 데이터
        if (churchEventRepository.count() == 0) {
            ChurchEvent event1 = ChurchEvent.builder()
                    .eventName("청년부 수련회")
                    .eventDescription("청년부 가을 수련회가 있습니다.")
                    .eventDate(today.plusDays(7))
                    .eventTime(LocalTime.of(9, 0))
                    .location("수양관")
                    .organizer("청년부")
                    .build();
            
            ChurchEvent event2 = ChurchEvent.builder()
                    .eventName("어린이 찬양제")
                    .eventDescription("어린이들의 찬양 발표회입니다.")
                    .eventDate(today.plusDays(14))
                    .eventTime(LocalTime.of(14, 0))
                    .location("본당")
                    .organizer("교육부")
                    .build();
            
            churchEventRepository.save(event1);
            churchEventRepository.save(event2);
            
            log.info("교회 행사 샘플 데이터 생성 완료");
        }
        
        log.info("샘플 데이터 초기화 완료");
    }
}
