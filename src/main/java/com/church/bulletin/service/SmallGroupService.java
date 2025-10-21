package com.church.bulletin.service;

import com.church.bulletin.entity.SmallGroup;
import com.church.bulletin.repository.SmallGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SmallGroupService {
    
    private final SmallGroupRepository smallGroupRepository;
    private final CloudinaryService cloudinaryService;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    // 업로드 디렉토리 경로
    private static final String UPLOAD_DIR = "uploads/small-groups";
    
    /**
     * 모든 활성 순모임 조회
     */
    @Transactional(readOnly = true)
    public List<SmallGroup> getAllActiveSmallGroups() {
        return smallGroupRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    /**
     * ID로 순모임 조회
     */
    @Transactional(readOnly = true)
    public SmallGroup getSmallGroupById(Long id) {
        return smallGroupRepository.findById(id).orElse(null);
    }
    
    /**
     * 순모임 생성
     */
    public SmallGroup createSmallGroup(SmallGroup smallGroup) {
        // 기본값 설정
        if (smallGroup.getIsActive() == null) {
            smallGroup.setIsActive(true);
        }
        if (smallGroup.getMemberCount() == null) {
            smallGroup.setMemberCount(0);
        }
        
        // 파일 업로드 처리
        processFileUploads(smallGroup);
        
        return smallGroupRepository.save(smallGroup);
    }
    
    /**
     * 순모임 수정
     */
    public SmallGroup updateSmallGroup(SmallGroup smallGroup) {
        Optional<SmallGroup> existingOpt = smallGroupRepository.findById(smallGroup.getId());
        if (existingOpt.isEmpty()) {
            return null;
        }
        
        SmallGroup existing = existingOpt.get();
        existing.setName(smallGroup.getName());
        existing.setLeader(smallGroup.getLeader());
        existing.setMemberCount(smallGroup.getMemberCount());
        existing.setLocation(smallGroup.getLocation());
        existing.setMeetingTime(smallGroup.getMeetingTime());
        existing.setDescription(smallGroup.getDescription());
        existing.setCategory(smallGroup.getCategory());
        
        // 파일 업로드 처리
        processFileUploads(smallGroup);
        
        // 기존 이미지/비디오와 새로 업로드된 파일 합치기
        if (smallGroup.getImageFiles() != null && !smallGroup.getImageFiles().isEmpty()) {
            existing.setImages(smallGroup.getImages());
        }
        if (smallGroup.getVideoFiles() != null && !smallGroup.getVideoFiles().isEmpty()) {
            existing.setVideos(smallGroup.getVideos());
        }
        
        return smallGroupRepository.save(existing);
    }
    
    /**
     * 순모임 삭제
     */
    public boolean deleteSmallGroup(Long id) {
        Optional<SmallGroup> smallGroupOpt = smallGroupRepository.findById(id);
        if (smallGroupOpt.isEmpty()) {
            return false;
        }
        
        smallGroupRepository.delete(smallGroupOpt.get());
        return true;
    }
    
    /**
     * 카테고리별 순모임 조회
     */
    @Transactional(readOnly = true)
    public List<SmallGroup> getSmallGroupsByCategory(String category) {
        return smallGroupRepository.findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(category);
    }
    
    /**
     * 순모임 검색
     */
    @Transactional(readOnly = true)
    public List<SmallGroup> searchSmallGroups(String keyword) {
        List<SmallGroup> byName = smallGroupRepository.findByNameContainingIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(keyword);
        List<SmallGroup> byLeader = smallGroupRepository.findByLeaderContainingIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(keyword);
        
        // 중복 제거
        byName.addAll(byLeader);
        return byName.stream().distinct().toList();
    }
    
    /**
     * 파일 업로드 처리
     */
    private void processFileUploads(SmallGroup smallGroup) {
        List<String> imageUrls = new ArrayList<>();
        List<String> videoUrls = new ArrayList<>();
        
        // 이미지 파일 처리
        if (smallGroup.getImageFiles() != null && !smallGroup.getImageFiles().isEmpty()) {
            for (MultipartFile file : smallGroup.getImageFiles()) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String imageUrl = uploadFile(file, "small-groups");
                        if (imageUrl != null) {
                            imageUrls.add(imageUrl);
                        }
                    } catch (Exception e) {
                        log.error("이미지 업로드 실패: {}", file.getOriginalFilename(), e);
                    }
                }
            }
        }
        
        // 비디오 파일 처리
        if (smallGroup.getVideoFiles() != null && !smallGroup.getVideoFiles().isEmpty()) {
            for (MultipartFile file : smallGroup.getVideoFiles()) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String videoUrl = uploadFile(file, "small-groups");
                        if (videoUrl != null) {
                            videoUrls.add(videoUrl);
                        }
                    } catch (Exception e) {
                        log.error("비디오 업로드 실패: {}", file.getOriginalFilename(), e);
                    }
                }
            }
        }
        
        // URL 문자열로 저장
        if (!imageUrls.isEmpty()) {
            smallGroup.setImages(String.join(",", imageUrls));
        }
        if (!videoUrls.isEmpty()) {
            smallGroup.setVideos(String.join(",", videoUrls));
        }
    }
    
    /**
     * 파일 업로드 (Cloudinary 또는 로컬)
     */
    private String uploadFile(MultipartFile file, String folder) {
        try {
            if ("railway".equals(activeProfile)) {
                // Cloudinary 사용
                return cloudinaryService.uploadImage(file, folder);
            } else {
                // 로컬 파일 시스템 사용
                String fileName = file.getOriginalFilename();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String savedFileName = timestamp + "_" + fileName;
                
                // 업로드 디렉토리 생성
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);
                
                // 파일 저장
                Path filePath = uploadPath.resolve(savedFileName);
                Files.copy(file.getInputStream(), filePath);
                
                // 웹에서 접근 가능한 URL 생성
                return "/" + UPLOAD_DIR + "/" + savedFileName;
            }
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }
}
