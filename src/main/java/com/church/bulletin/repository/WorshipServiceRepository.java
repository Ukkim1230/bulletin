package com.church.bulletin.repository;

import com.church.bulletin.entity.WorshipService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorshipServiceRepository extends JpaRepository<WorshipService, Long> {
    
    // 특정 날짜의 예배 정보 조회
    List<WorshipService> findByServiceDateOrderByServiceTime(LocalDate serviceDate);
    
    // 특정 날짜와 예배 유형으로 조회
    Optional<WorshipService> findByServiceDateAndServiceType(LocalDate serviceDate, String serviceType);
    
    // 날짜 범위로 예배 정보 조회
    List<WorshipService> findByServiceDateBetweenOrderByServiceDateDescServiceTimeAsc(
            LocalDate startDate, LocalDate endDate);
    
    // 최근 예배 정보 조회
    @Query("SELECT w FROM WorshipService w WHERE w.serviceDate <= :currentDate ORDER BY w.serviceDate DESC, w.serviceTime DESC")
    List<WorshipService> findRecentServices(@Param("currentDate") LocalDate currentDate);
    
    // 다음 주 예배 정보 조회
    @Query("SELECT w FROM WorshipService w WHERE w.serviceDate > :currentDate ORDER BY w.serviceDate ASC, w.serviceTime ASC")
    List<WorshipService> findUpcomingServices(@Param("currentDate") LocalDate currentDate);
}
