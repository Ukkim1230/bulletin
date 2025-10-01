package com.church.bulletin.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    /**
     * Cloudinary에 이미지 업로드
     * @param file 업로드할 파일
     * @param folder 저장할 폴더명
     * @return 업로드된 이미지 URL
     */
    public String uploadImage(MultipartFile file, String folder) {
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image"
            );
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String imageUrl = (String) uploadResult.get("secure_url");
            
            log.info("Cloudinary 이미지 업로드 성공: {}", imageUrl);
            return imageUrl;
            
        } catch (IOException e) {
            log.error("Cloudinary 이미지 업로드 실패", e);
            throw new RuntimeException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Cloudinary에서 이미지 삭제
     * @param publicId 삭제할 이미지의 public ID
     */
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Cloudinary 이미지 삭제 성공: {}", publicId);
        } catch (IOException e) {
            log.error("Cloudinary 이미지 삭제 실패", e);
            throw new RuntimeException("이미지 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * URL에서 public ID 추출
     * @param imageUrl Cloudinary 이미지 URL
     * @return public ID
     */
    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return null;
        }
        
        // URL 예: https://res.cloudinary.com/cloud-name/image/upload/v1234567890/folder/filename.jpg
        // public ID: folder/filename
        String[] parts = imageUrl.split("/upload/");
        if (parts.length > 1) {
            String pathAfterUpload = parts[1];
            // 버전 번호 제거 (v1234567890/)
            String withoutVersion = pathAfterUpload.replaceFirst("v\\d+/", "");
            // 확장자 제거
            int lastDot = withoutVersion.lastIndexOf('.');
            if (lastDot > 0) {
                return withoutVersion.substring(0, lastDot);
            }
            return withoutVersion;
        }
        
        return null;
    }
}
