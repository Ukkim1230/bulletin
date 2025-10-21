package com.church.bulletin.repository;

import com.church.bulletin.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    // 활성화된 공지사항 조회 (표시 순서대로)
    List<Announcement> findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();
    
    // 중요 공지사항 조회
    List<Announcement> findByIsActiveTrueAndIsImportantTrueOrderByDisplayOrderAscCreatedAtDesc();
    
    // 특정 날짜의 공지사항 조회
    List<Announcement> findByAnnouncementDateAndIsActiveTrueOrderByDisplayOrderAsc(LocalDate announcementDate);
    
    // 현재 유효한 공지사항 조회 (기간 고려)
    @Query("SELECT a FROM Announcement a WHERE a.isActive = true " +
           "AND (a.startDate IS NULL OR a.startDate <= :currentDate) " +
           "AND (a.endDate IS NULL OR a.endDate >= :currentDate) " +
           "ORDER BY a.displayOrder ASC, a.createdAt DESC")
    List<Announcement> findCurrentActiveAnnouncements(@Param("currentDate") LocalDate currentDate);
    
    // 날짜 범위 내 공지사항 조회
    List<Announcement> findByAnnouncementDateBetweenAndIsActiveTrueOrderByAnnouncementDateDescDisplayOrderAsc(
            LocalDate startDate, LocalDate endDate);
    
    // 활성화된 공지사항 조회 (시작일/종료일 고려)
    List<Announcement> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByDisplayOrderAscCreatedAtDesc(
            LocalDate startDate, LocalDate endDate);
}
