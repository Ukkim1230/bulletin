package com.church.bulletin.repository;

import com.church.bulletin.entity.SheetMusic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SheetMusicRepository extends JpaRepository<SheetMusic, Long> {
    
    /**
     * 즐겨찾기 악보 조회
     */
    List<SheetMusic> findByIsFavoriteTrueOrderByCreatedAtDesc();
    
    /**
     * 난이도별 악보 조회
     */
    List<SheetMusic> findByDifficulty(SheetMusic.Difficulty difficulty);
    
    /**
     * 제목 또는 작곡가로 검색
     */
    @Query("SELECT s FROM SheetMusic s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.composer) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<SheetMusic> findByTitleOrComposerContaining(@Param("searchTerm") String searchTerm);
    
    /**
     * 페이징된 악보 조회 (최신순)
     */
    Page<SheetMusic> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 조성별 악보 조회
     */
    List<SheetMusic> findByKeySignature(String keySignature);
    
    /**
     * 빠르기별 악보 조회
     */
    List<SheetMusic> findByTempo(String tempo);
}

