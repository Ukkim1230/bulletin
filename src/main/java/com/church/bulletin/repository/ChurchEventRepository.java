package com.church.bulletin.repository;

import com.church.bulletin.entity.ChurchEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChurchEventRepository extends JpaRepository<ChurchEvent, Long> {
    
    // 다가오는 행사 조회
    @Query("SELECT e FROM ChurchEvent e WHERE e.eventDate >= :currentDate ORDER BY e.eventDate ASC, e.eventTime ASC")
    List<ChurchEvent> findUpcomingEvents(@Param("currentDate") LocalDate currentDate);
    
    // 특정 날짜의 행사 조회
    List<ChurchEvent> findByEventDateOrderByEventTimeAsc(LocalDate eventDate);
    
    // 날짜 범위 내 행사 조회
    List<ChurchEvent> findByEventDateBetweenOrderByEventDateAscEventTimeAsc(LocalDate startDate, LocalDate endDate);
    
    // 정기 행사 조회
    List<ChurchEvent> findByIsRecurringTrueOrderByEventDateAsc();
    
    // 주관 부서별 행사 조회
    List<ChurchEvent> findByOrganizerOrderByEventDateAsc(String organizer);
}
