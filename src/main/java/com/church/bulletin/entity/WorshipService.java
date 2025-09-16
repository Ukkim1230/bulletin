package com.church.bulletin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "worship_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorshipService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;
    
    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType; // 주일예배, 수요예배, 금요기도회 등
    
    @Column(name = "service_time", nullable = false)
    private LocalTime serviceTime;
    
    @Column(name = "preacher", nullable = false, length = 100)
    private String preacher; // 설교자
    
    @Column(name = "sermon_title", nullable = false, length = 200)
    private String sermonTitle; // 설교제목
    
    @Column(name = "sermon_text", length = 200)
    private String sermonText; // 본문 말씀
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 - 찬양 정보
    @OneToMany(mappedBy = "worshipService", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PraiseSong> praiseSongs = new ArrayList<>();
    
    // 편의 메서드
    public void addPraiseSong(PraiseSong praiseSong) {
        this.praiseSongs.add(praiseSong);
        praiseSong.setWorshipService(this);
    }
}
