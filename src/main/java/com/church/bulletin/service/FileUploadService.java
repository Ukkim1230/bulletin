package com.church.bulletin.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {
    
    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param uploadDir 업로드 디렉토리
     * @return 업로드 결과
     */
    public FileUploadResult uploadFile(MultipartFile file, String uploadDir) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }
        
        try {
            // 원본 파일명과 확장자 추출
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            // 고유한 파일명 생성 (타임스탬프 + UUID)
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String savedFileName = timestamp + "_" + uuid + extension;
            
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            
            // 파일 저장
            Path filePath = uploadPath.resolve(savedFileName);
            Files.copy(file.getInputStream(), filePath);
            
            log.info("파일 업로드 성공: {}", filePath.toString());
            
            return FileUploadResult.builder()
                    .originalFileName(originalFileName)
                    .fileName(savedFileName)
                    .filePath(uploadDir + savedFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .build();
                    
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 파일 삭제
     * @param filePath 삭제할 파일 경로
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("파일 삭제 성공: {}", filePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패", e);
            throw new RuntimeException("파일 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadResult {
        private String originalFileName;
        private String fileName;
        private String filePath;
        private Long fileSize;
        private String contentType;
    }
}
