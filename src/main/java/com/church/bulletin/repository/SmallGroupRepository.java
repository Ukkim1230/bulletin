package com.church.bulletin.repository;

import com.church.bulletin.entity.SmallGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SmallGroupRepository extends JpaRepository<SmallGroup, Long> {
    
    // 활성화된 순모임 조회
    List<SmallGroup> findByIsActiveTrueOrderByCreatedAtDesc();
    
    // 카테고리별 순모임 조회
    List<SmallGroup> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(String category);
    
    // 순장으로 검색
    List<SmallGroup> findByLeaderContainingIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(String leader);
    
    // 이름으로 검색
    List<SmallGroup> findByNameContainingIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(String name);
}
