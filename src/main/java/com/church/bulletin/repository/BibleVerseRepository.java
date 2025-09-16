package com.church.bulletin.repository;

import com.church.bulletin.entity.BibleVerse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BibleVerseRepository extends JpaRepository<BibleVerse, Long> {
    
    // 특정 날짜의 성경구절 조회
    List<BibleVerse> findByServiceDate(LocalDate serviceDate);
    
    // 특정 날짜와 유형의 성경구절 조회
    Optional<BibleVerse> findByServiceDateAndVerseType(LocalDate serviceDate, BibleVerse.VerseType verseType);
    
    // 최근 성경구절 조회
    List<BibleVerse> findByServiceDateLessThanEqualOrderByServiceDateDesc(LocalDate currentDate);
    
    // 날짜 범위 내 성경구절 조회
    List<BibleVerse> findByServiceDateBetweenOrderByServiceDateDesc(LocalDate startDate, LocalDate endDate);
    
    // 주간 말씀 조회
    List<BibleVerse> findByVerseTypeOrderByServiceDateDesc(BibleVerse.VerseType verseType);
}
