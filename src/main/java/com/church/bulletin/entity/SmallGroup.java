package com.church.bulletin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "small_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmallGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "leader", nullable = false, length = 50)
    private String leader;
    
    @Column(name = "member_count")
    @Builder.Default
    private Integer memberCount = 0;
    
    @Column(name = "location", nullable = false, length = 200)
    private String location;
    
    @Column(name = "meeting_time", nullable = false, length = 100)
    private String meetingTime;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "images", columnDefinition = "TEXT")
    private String images; // JSON 형태로 저장
    
    @Column(name = "videos", columnDefinition = "TEXT")
    private String videos; // JSON 형태로 저장
    
    // 파일 업로드 관련 필드 (데이터베이스에 저장되지 않음)
    @Transient
    private List<MultipartFile> imageFiles;
    
    @Transient
    private List<MultipartFile> videoFiles;
    
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
    public List<String> getImageList() {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return List.of(images.split(","));
    }
    
    public List<String> getVideoList() {
        if (videos == null || videos.isEmpty()) {
            return List.of();
        }
        return List.of(videos.split(","));
    }
    
    public void setImageList(List<String> imageList) {
        this.images = String.join(",", imageList);
    }
    
    public void setVideoList(List<String> videoList) {
        this.videos = String.join(",", videoList);
    }
}
