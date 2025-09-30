package com.church.bulletin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sheet_music")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SheetMusic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "song_number", length = 10)
    private String songNumber; // 찬송가 번호 (선택사항)
    
    @Column(name = "composer", length = 100)
    private String composer; // 작곡가 (선택사항)
    
    @Column(name = "key_signature", length = 20)
    private String keySignature; // 조성 (C장조, G장조 등)
    
    @Column(name = "tempo", length = 20)
    private String tempo; // 빠르기 (Largo, Andante 등)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 10)
    private Difficulty difficulty;
    
    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl; // 악보 이미지 URL
    
    @Column(name = "file_name", length = 255)
    private String fileName; // 업로드된 파일명
    
    @Column(name = "file_path", length = 500)
    private String filePath; // 파일 저장 경로
    
    @Column(name = "file_size")
    private Long fileSize; // 파일 크기 (bytes)
    
    @Column(name = "content_type", length = 100)
    private String contentType; // 파일 MIME 타입
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enum 정의
    public enum Difficulty {
        EASY("쉬움"),
        MEDIUM("보통"),
        HARD("어려움");
        
        private final String displayName;
        
        Difficulty(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
