package com.church.bulletin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "announcement_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate announcementDate; // 광고 게시 날짜
    
    @Column(name = "start_date")
    private LocalDate startDate; // 행사 시작일
    
    @Column(name = "end_date")
    private LocalDate endDate; // 행사 종료일
    
    @Column(name = "is_important")
    @Builder.Default
    private Boolean isImportant = false; // 중요 공지 여부
    
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0; // 표시 순서
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 편의 메서드
    public boolean isCurrentlyActive() {
        LocalDate now = LocalDate.now();
        if (!isActive) return false;
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        return true;
    }
}
