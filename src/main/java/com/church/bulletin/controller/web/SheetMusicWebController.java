package com.church.bulletin.controller.web;

import com.church.bulletin.entity.SheetMusic;
import com.church.bulletin.service.SheetMusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sheet-music")
@RequiredArgsConstructor
@Slf4j
public class SheetMusicWebController {
    
    private final SheetMusicService sheetMusicService;
    
    /**
     * 악보 라이브러리 메인 페이지
     */
    @GetMapping
    public String sheetMusicLibrary(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 12) Pageable pageable,
            Model model) {
        
        try {
            log.info("악보 라이브러리 접근 - search: {}", search);
            
            Page<SheetMusic> sheetMusicPage;
            
            if (search != null && !search.trim().isEmpty()) {
                // 검색어로 필터링
                log.info("검색어로 필터링: {}", search);
                List<SheetMusic> searchResults = sheetMusicService.searchSheetMusic(search.trim());
                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), searchResults.size());
                List<SheetMusic> pageContent = start < searchResults.size() ? 
                    searchResults.subList(start, end) : new ArrayList<>();
                
                sheetMusicPage = new PageImpl<>(pageContent, pageable, searchResults.size());
            } else {
                // 전체 조회
                log.info("전체 악보 조회");
                sheetMusicPage = sheetMusicService.getSheetMusicPage(pageable);
            }
            
            log.info("악보 페이지 로드 완료 - 총 {}개", sheetMusicPage.getTotalElements());
            
            model.addAttribute("sheetMusicPage", sheetMusicPage);
            model.addAttribute("searchTerm", search);
            model.addAttribute("difficulties", SheetMusic.Difficulty.values());
            
            // 통계 정보
            log.info("통계 정보 로드 시작");
            model.addAttribute("totalCount", sheetMusicService.getSheetMusicCount());
            log.info("통계 정보 로드 완료");
            
            return "sheet-music/library";
        } catch (Exception e) {
            log.error("악보 라이브러리 로드 실패 - 상세 오류:", e);
            e.printStackTrace();
            model.addAttribute("error", "악보를 불러오는 중 오류가 발생했습니다: " + e.getMessage() + " - " + e.getClass().getSimpleName());
            return "error/500";
        }
    }
    
    /**
     * 악보 상세 페이지
     */
    @GetMapping("/{id}")
    public String sheetMusicDetail(@PathVariable Long id, Model model) {
        return sheetMusicService.getSheetMusicById(id)
                .map(sheetMusic -> {
                    model.addAttribute("sheetMusic", sheetMusic);
                    
                    // 다른 악보들 (최대 4개, 최신순)
                    List<SheetMusic> relatedMusic = sheetMusicService.getAllSheetMusic()
                            .stream()
                            .filter(music -> !music.getId().equals(id))
                            .limit(4)
                            .toList();
                    model.addAttribute("relatedMusic", relatedMusic);
                    
                    return "sheet-music/detail";
                })
                .orElse("error/404");
    }
    
    /**
     * 즐겨찾기 악보 페이지
     */
    @GetMapping("/favorites")
    public String favoriteSheetMusic(Model model) {
        List<SheetMusic> favorites = sheetMusicService.getFavoriteSheetMusic();
        model.addAttribute("favorites", favorites);
        return "sheet-music/favorites";
    }
    
    /**
     * 난이도별 악보 페이지
     */
    @GetMapping("/difficulty/{difficulty}")
    public String sheetMusicByDifficulty(@PathVariable SheetMusic.Difficulty difficulty, Model model) {
        List<SheetMusic> sheetMusic = sheetMusicService.getSheetMusicByDifficulty(difficulty);
        model.addAttribute("sheetMusic", sheetMusic);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("difficulties", SheetMusic.Difficulty.values());
        return "sheet-music/by-difficulty";
    }
    
    /**
     * 악보 관리 페이지 (관리자용)
     */
    @GetMapping("/admin")
    public String adminSheetMusic(@PageableDefault(size = 20) Pageable pageable, Model model) {
        Page<SheetMusic> sheetMusicPage = sheetMusicService.getSheetMusicPage(pageable);
        model.addAttribute("sheetMusicPage", sheetMusicPage);
        model.addAttribute("difficulties", SheetMusic.Difficulty.values());
        return "sheet-music/admin";
    }
    
    /**
     * 사용자용 악보 추가 페이지
     */
    @GetMapping("/add")
    public String addSheetMusicForm(Model model) {
        model.addAttribute("sheetMusic", new SheetMusic());
        model.addAttribute("difficulties", SheetMusic.Difficulty.values());
        return "sheet-music/simple-add";
    }
    
    /**
     * 관리자용 악보 추가 페이지
     */
    @GetMapping("/admin/add")
    public String adminAddSheetMusicForm(Model model) {
        model.addAttribute("sheetMusic", new SheetMusic());
        model.addAttribute("difficulties", SheetMusic.Difficulty.values());
        return "sheet-music/form";
    }
    
    /**
     * 악보 수정 페이지
     */
    @GetMapping("/admin/edit/{id}")
    public String editSheetMusicForm(@PathVariable Long id, Model model) {
        return sheetMusicService.getSheetMusicById(id)
                .map(sheetMusic -> {
                    model.addAttribute("sheetMusic", sheetMusic);
                    model.addAttribute("difficulties", SheetMusic.Difficulty.values());
                    return "sheet-music/form";
                })
                .orElse("error/404");
    }
    
    /**
     * 사용자용 악보 추가 처리
     */
    @PostMapping("/add")
    public String addSheetMusic(@ModelAttribute SheetMusic sheetMusic, 
                               @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            sheetMusicService.saveSheetMusicWithFile(sheetMusic, file);
            return "redirect:/sheet-music?success=added";
        } catch (Exception e) {
            log.error("악보 추가 실패", e);
            return "redirect:/sheet-music?error=add_failed";
        }
    }
    
    /**
     * 관리자용 악보 추가 처리
     */
    @PostMapping("/admin")
    public String adminAddSheetMusic(@ModelAttribute SheetMusic sheetMusic) {
        try {
            sheetMusicService.saveSheetMusic(sheetMusic);
            return "redirect:/sheet-music/admin?success=added";
        } catch (Exception e) {
            log.error("악보 추가 실패", e);
            return "redirect:/sheet-music/admin?error=add_failed";
        }
    }
    
    /**
     * 악보 수정 처리
     */
    @PostMapping("/admin/{id}")
    public String updateSheetMusic(@PathVariable Long id, @ModelAttribute SheetMusic sheetMusic) {
        try {
            sheetMusicService.updateSheetMusic(id, sheetMusic);
            return "redirect:/sheet-music/admin?success=updated";
        } catch (Exception e) {
            log.error("악보 수정 실패", e);
            return "redirect:/sheet-music/admin?error=update_failed";
        }
    }
    
    /**
     * 악보 삭제 처리
     */
    @PostMapping("/admin/{id}/delete")
    public String deleteSheetMusic(@PathVariable Long id) {
        try {
            sheetMusicService.deleteSheetMusic(id);
            return "redirect:/sheet-music/admin?success=deleted";
        } catch (Exception e) {
            log.error("악보 삭제 실패", e);
            return "redirect:/sheet-music/admin?error=delete_failed";
        }
    }
    
    /**
     * 악보 파일 다운로드
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadSheetMusic(@PathVariable Long id) {
        Optional<SheetMusic> sheetMusicOptional = sheetMusicService.getSheetMusicById(id);
        if (sheetMusicOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        SheetMusic sheetMusic = sheetMusicOptional.get();
        if (sheetMusic.getFilePath() == null || sheetMusic.getFilePath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            File file = new File(sheetMusic.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = sheetMusic.getContentType();
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 파일 이름 인코딩 (한글 지원)
            String encodedFileName = java.net.URLEncoder.encode(sheetMusic.getFileName(), "UTF-8")
                    .replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + sheetMusic.getFileName() + "\"; filename*=UTF-8''" + encodedFileName)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("파일 다운로드 실패: {}", sheetMusic.getFilePath(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 악보 파일 뷰어
     */
    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewSheetMusic(@PathVariable Long id) {
        Optional<SheetMusic> sheetMusicOptional = sheetMusicService.getSheetMusicById(id);
        if (sheetMusicOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SheetMusic sheetMusic = sheetMusicOptional.get();
        if (sheetMusic.getFilePath() == null || sheetMusic.getFilePath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            File file = new File(sheetMusic.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = sheetMusic.getContentType();
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                    .body(resource);

        } catch (IOException e) {
            log.error("파일 뷰어 실패: {}", sheetMusic.getFilePath(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
