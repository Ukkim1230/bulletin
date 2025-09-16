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
@Table(name = "bible_verses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BibleVerse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;
    
    @Column(name = "verse_reference", nullable = false, length = 100)
    private String verseReference; // 예: "요한복음 3:16"
    
    @Column(name = "verse_text", nullable = false, columnDefinition = "TEXT")
    private String verseText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "verse_type", length = 20)
    @Builder.Default
    private VerseType verseType = VerseType.WEEKLY;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Enum 정의
    public enum VerseType {
        WEEKLY,   // 주간 말씀
        DAILY,    // 일일 말씀
        SPECIAL   // 특별 말씀
    }
}
