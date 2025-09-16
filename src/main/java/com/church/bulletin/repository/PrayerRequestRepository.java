package com.church.bulletin.repository;

import com.church.bulletin.entity.PrayerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrayerRequestRepository extends JpaRepository<PrayerRequest, Long> {
    
    // 특정 날짜의 공개 기도제목 조회
    List<PrayerRequest> findByRequestDateAndIsPublicTrueOrderByCreatedAtDesc(LocalDate requestDate);
    
    // 카테고리별 공개 기도제목 조회
    List<PrayerRequest> findByCategoryAndIsPublicTrueOrderByRequestDateDescCreatedAtDesc(String category);
    
    // 날짜 범위 내 공개 기도제목 조회
    List<PrayerRequest> findByRequestDateBetweenAndIsPublicTrueOrderByRequestDateDescCreatedAtDesc(
            LocalDate startDate, LocalDate endDate);
    
    // 최근 공개 기도제목 조회
    List<PrayerRequest> findByIsPublicTrueOrderByRequestDateDescCreatedAtDesc();
}
