package com.church.bulletin.service;

import com.church.bulletin.entity.SheetMusic;
import com.church.bulletin.repository.SheetMusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class SheetMusicService {
    
    private final SheetMusicRepository sheetMusicRepository;
    private final CloudinaryService cloudinaryService;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    public SheetMusicService(SheetMusicRepository sheetMusicRepository, 
                            CloudinaryService cloudinaryService) {
        this.sheetMusicRepository = sheetMusicRepository;
        this.cloudinaryService = cloudinaryService;
    }
    
    // 업로드 디렉토리 경로
    private static final String UPLOAD_DIR = "uploads/sheet-music";
    
    /**
     * 모든 악보 조회
     */
    @Transactional(readOnly = true)
    public List<SheetMusic> getAllSheetMusic() {
        return sheetMusicRepository.findAll();
    }
    
    /**
     * 페이징된 악보 조회
     */
    @Transactional(readOnly = true)
    public Page<SheetMusic> getSheetMusicPage(Pageable pageable) {
        return sheetMusicRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * ID로 악보 조회
     */
    @Transactional(readOnly = true)
    public Optional<SheetMusic> getSheetMusicById(Long id) {
        return sheetMusicRepository.findById(id);
    }
    
    /**
     * 즐겨찾기 악보 조회
     */
    @Transactional(readOnly = true)
    public List<SheetMusic> getFavoriteSheetMusic() {
        return sheetMusicRepository.findByIsFavoriteTrueOrderByCreatedAtDesc();
    }
    
    /**
     * 난이도별 악보 조회
     */
    @Transactional(readOnly = true)
    public List<SheetMusic> getSheetMusicByDifficulty(SheetMusic.Difficulty difficulty) {
        return sheetMusicRepository.findByDifficulty(difficulty);
    }
    
    /**
     * 악보 검색
     */
    @Transactional(readOnly = true)
    public List<SheetMusic> searchSheetMusic(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllSheetMusic();
        }
        return sheetMusicRepository.findByTitleOrComposerContaining(searchTerm.trim());
    }
    
    /**
     * 조성별 악보 조회
     */
    @Transactional(readOnly = true)
    public List<SheetMusic> getSheetMusicByKeySignature(String keySignature) {
        return sheetMusicRepository.findByKeySignature(keySignature);
    }
    
    /**
     * 빠르기별 악보 조회
     */
    @Transactional(readOnly = true)
    public List<SheetMusic> getSheetMusicByTempo(String tempo) {
        return sheetMusicRepository.findByTempo(tempo);
    }
    
    /**
     * 악보 저장
     */
    public SheetMusic saveSheetMusic(SheetMusic sheetMusic) {
        return sheetMusicRepository.save(sheetMusic);
    }
    
    /**
     * 파일과 함께 악보 저장
     */
    public SheetMusic saveSheetMusicWithFile(SheetMusic sheetMusic, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                String contentType = file.getContentType();
                boolean isImage = contentType != null && contentType.startsWith("image/");
                
                // EC2 프로필이면 로컬 파일 시스템 사용, 그 외에는 Cloudinary 사용
                if (!"ec2".equals(activeProfile)) {
                    // Cloudinary 사용
                    log.info("Cloudinary를 사용하여 파일 업로드 시작");
                    String imageUrl = cloudinaryService.uploadImage(file, "sheet-music");
                    
                    sheetMusic.setFileName(file.getOriginalFilename());
                    sheetMusic.setImageUrl(imageUrl);
                    sheetMusic.setFileSize(file.getSize());
                    sheetMusic.setContentType(contentType);
                    
                    log.info("Cloudinary 업로드 성공: {}", imageUrl);
                } else {
                    // 로컬 파일 시스템 사용
                    log.info("로컬 파일 시스템을 사용하여 파일 업로드 시작");
                    String fileName = file.getOriginalFilename();
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String savedFileName = timestamp + "_" + fileName;
                    
                    // 업로드 디렉토리 생성
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    Files.createDirectories(uploadPath);
                    
                    // 파일 저장
                    Path filePath = uploadPath.resolve(savedFileName);
                    Files.copy(file.getInputStream(), filePath);
                    
                    // 악보 정보에 파일 정보 설정
                    sheetMusic.setFileName(fileName);
                    sheetMusic.setFilePath(filePath.toString());
                    sheetMusic.setFileSize(file.getSize());
                    sheetMusic.setContentType(contentType);
                    
                    // 이미지 파일인 경우 imageUrl도 설정
                    if (isImage) {
                        // 웹에서 접근 가능한 URL 생성
                        String imageUrl = "/" + UPLOAD_DIR + "/" + savedFileName;
                        sheetMusic.setImageUrl(imageUrl);
                        log.info("이미지 URL 설정: {}", imageUrl);
                    }
                    
                    log.info("파일 업로드 성공: {}", filePath.toString());
                }
            } catch (Exception e) {
                log.error("파일 업로드 실패", e);
                throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
            }
        }
        
        return saveSheetMusic(sheetMusic);
    }
    
    /**
     * 악보 수정
     */
    public SheetMusic updateSheetMusic(Long id, SheetMusic updatedSheetMusic) {
        SheetMusic existingSheetMusic = sheetMusicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("악보를 찾을 수 없습니다: " + id));
        
        // 기존 정보 업데이트
        existingSheetMusic.setTitle(updatedSheetMusic.getTitle());
        existingSheetMusic.setSongNumber(updatedSheetMusic.getSongNumber());
        existingSheetMusic.setComposer(updatedSheetMusic.getComposer());
        existingSheetMusic.setKeySignature(updatedSheetMusic.getKeySignature());
        existingSheetMusic.setTempo(updatedSheetMusic.getTempo());
        existingSheetMusic.setDifficulty(updatedSheetMusic.getDifficulty());
        existingSheetMusic.setIsFavorite(updatedSheetMusic.getIsFavorite());
        existingSheetMusic.setImageUrl(updatedSheetMusic.getImageUrl());
        
        return sheetMusicRepository.save(existingSheetMusic);
    }
    
    /**
     * 즐겨찾기 토글
     */
    public SheetMusic toggleFavorite(Long id) {
        SheetMusic sheetMusic = sheetMusicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("악보를 찾을 수 없습니다: " + id));
        
        sheetMusic.setIsFavorite(!sheetMusic.getIsFavorite());
        return sheetMusicRepository.save(sheetMusic);
    }
    
    /**
     * 악보 삭제
     */
    public void deleteSheetMusic(Long id) {
        SheetMusic sheetMusic = sheetMusicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("악보를 찾을 수 없습니다: " + id));
        
        // EC2 프로필이 아니면 Cloudinary 이미지 삭제
        if (!"ec2".equals(activeProfile)) {
            if (sheetMusic.getImageUrl() != null && !sheetMusic.getImageUrl().isEmpty()) {
                try {
                    String publicId = cloudinaryService.extractPublicId(sheetMusic.getImageUrl());
                    if (publicId != null) {
                        cloudinaryService.deleteImage(publicId);
                        log.info("Cloudinary 이미지 삭제 완료: {}", publicId);
                    }
                } catch (Exception e) {
                    log.warn("Cloudinary 이미지 삭제 실패: {}", sheetMusic.getImageUrl(), e);
                }
            }
        } else {
            // 로컬 파일 삭제
            if (sheetMusic.getFilePath() != null && !sheetMusic.getFilePath().isEmpty()) {
                try {
                    File file = new File(sheetMusic.getFilePath());
                    if (file.exists()) {
                        file.delete();
                        log.info("파일 삭제 완료: {}", sheetMusic.getFilePath());
                    }
                } catch (Exception e) {
                    log.warn("파일 삭제 실패: {}", sheetMusic.getFilePath(), e);
                }
            }
        }
        
        sheetMusicRepository.delete(sheetMusic);
    }
    
    /**
     * 전체 악보 개수 조회
     */
    @Transactional(readOnly = true)
    public long getSheetMusicCount() {
        return sheetMusicRepository.count();
    }
    
}
