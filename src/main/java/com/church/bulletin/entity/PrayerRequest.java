package com.church.bulletin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prayer_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;
    
    @Column(name = "category", length = 50)
    private String category; // 교회, 성도, 선교, 국가 등
    
    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true; // 공개 여부
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
