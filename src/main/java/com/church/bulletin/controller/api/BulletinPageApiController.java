package com.church.bulletin.controller.api;

import com.church.bulletin.entity.BulletinPage;
import com.church.bulletin.service.BulletinPageService;
import com.church.bulletin.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bulletin-pages")
@RequiredArgsConstructor
public class BulletinPageApiController {
    
    private final BulletinPageService bulletinPageService;
    private final CloudinaryService cloudinaryService;
    
    @GetMapping
    public ResponseEntity<List<BulletinPage>> getAllPages() {
        return ResponseEntity.ok(bulletinPageService.getAllPages());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BulletinPage> getPageById(@PathVariable Long id) {
        return ResponseEntity.ok(bulletinPageService.getPageById(id));
    }
    
    @PostMapping
    public ResponseEntity<BulletinPage> createPage(@RequestBody BulletinPage page) {
        return ResponseEntity.ok(bulletinPageService.createPage(page));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BulletinPage> updatePage(
            @PathVariable Long id,
            @RequestBody BulletinPage page) {
        return ResponseEntity.ok(bulletinPageService.updatePage(id, page));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        bulletinPageService.deletePage(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Cloudinary에 업로드
            String imageUrl = cloudinaryService.uploadImage(file, "bulletin-images");
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("fileName", file.getOriginalFilename());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "이미지 업로드에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
