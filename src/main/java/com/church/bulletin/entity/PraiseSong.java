package com.church.bulletin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "praise_songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PraiseSong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "song_order", nullable = false)
    private Integer songOrder; // 순서 (1: 전주, 2: 찬송1, 3: 찬송2 등)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "song_type", nullable = false, length = 20)
    private SongType songType; // HYMN, CCM, SPECIAL
    
    @Column(name = "song_title", nullable = false, length = 200)
    private String songTitle;
    
    @Column(name = "song_number", length = 10)
    private String songNumber; // 찬송가 번호 (CCM의 경우 NULL)
    
    @Column(name = "lyrics", columnDefinition = "TEXT")
    private String lyrics; // 가사 (선택사항)
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worship_service_id", nullable = false)
    private WorshipService worshipService;
    
    // Enum 정의
    public enum SongType {
        HYMN,    // 찬송가
        CCM,     // CCM
        SPECIAL  // 특송
    }
}
