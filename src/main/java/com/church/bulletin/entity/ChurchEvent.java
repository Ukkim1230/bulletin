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

@Entity
@Table(name = "church_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChurchEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_name", nullable = false, length = 200)
    private String eventName;
    
    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;
    
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;
    
    @Column(name = "event_time")
    private LocalTime eventTime;
    
    @Column(name = "location", length = 200)
    private String location;
    
    @Column(name = "organizer", length = 100)
    private String organizer; // 주관 부서/담당자
    
    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false; // 정기 행사 여부
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 편의 메서드
    public boolean isUpcoming() {
        return eventDate.isAfter(LocalDate.now()) || eventDate.isEqual(LocalDate.now());
    }
}
