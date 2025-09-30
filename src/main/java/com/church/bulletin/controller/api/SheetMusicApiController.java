package com.church.bulletin.controller.api;

import com.church.bulletin.entity.SheetMusic;
import com.church.bulletin.service.SheetMusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sheet-music")
@RequiredArgsConstructor
@Slf4j
public class SheetMusicApiController {
    
    private final SheetMusicService sheetMusicService;
    
    /**
     * 모든 악보 조회
     */
    @GetMapping
    public ResponseEntity<List<SheetMusic>> getAllSheetMusic() {
        List<SheetMusic> sheetMusic = sheetMusicService.getAllSheetMusic();
        return ResponseEntity.ok(sheetMusic);
    }
    
    /**
     * 페이징된 악보 조회
     */
    @GetMapping("/page")
    public ResponseEntity<Page<SheetMusic>> getSheetMusicPage(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<SheetMusic> sheetMusicPage = sheetMusicService.getSheetMusicPage(pageable);
        return ResponseEntity.ok(sheetMusicPage);
    }
    
    /**
     * ID로 악보 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<SheetMusic> getSheetMusicById(@PathVariable Long id) {
        return sheetMusicService.getSheetMusicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 즐겨찾기 악보 조회
     */
    @GetMapping("/favorites")
    public ResponseEntity<List<SheetMusic>> getFavoriteSheetMusic() {
        List<SheetMusic> favorites = sheetMusicService.getFavoriteSheetMusic();
        return ResponseEntity.ok(favorites);
    }
    
    /**
     * 난이도별 악보 조회
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<SheetMusic>> getSheetMusicByDifficulty(@PathVariable SheetMusic.Difficulty difficulty) {
        List<SheetMusic> sheetMusic = sheetMusicService.getSheetMusicByDifficulty(difficulty);
        return ResponseEntity.ok(sheetMusic);
    }
    
    /**
     * 악보 검색
     */
    @GetMapping("/search")
    public ResponseEntity<List<SheetMusic>> searchSheetMusic(@RequestParam String q) {
        List<SheetMusic> results = sheetMusicService.searchSheetMusic(q);
        return ResponseEntity.ok(results);
    }
    
    /**
     * 조성별 악보 조회
     */
    @GetMapping("/key/{keySignature}")
    public ResponseEntity<List<SheetMusic>> getSheetMusicByKeySignature(@PathVariable String keySignature) {
        List<SheetMusic> sheetMusic = sheetMusicService.getSheetMusicByKeySignature(keySignature);
        return ResponseEntity.ok(sheetMusic);
    }
    
    /**
     * 빠르기별 악보 조회
     */
    @GetMapping("/tempo/{tempo}")
    public ResponseEntity<List<SheetMusic>> getSheetMusicByTempo(@PathVariable String tempo) {
        List<SheetMusic> sheetMusic = sheetMusicService.getSheetMusicByTempo(tempo);
        return ResponseEntity.ok(sheetMusic);
    }
    
    /**
     * 새 악보 생성
     */
    @PostMapping
    public ResponseEntity<SheetMusic> createSheetMusic(@RequestBody SheetMusic sheetMusic) {
        try {
            SheetMusic createdMusic = sheetMusicService.saveSheetMusic(sheetMusic);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMusic);
        } catch (Exception e) {
            log.error("악보 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 악보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<SheetMusic> updateSheetMusic(@PathVariable Long id, @RequestBody SheetMusic sheetMusic) {
        try {
            SheetMusic updatedMusic = sheetMusicService.updateSheetMusic(id, sheetMusic);
            return ResponseEntity.ok(updatedMusic);
        } catch (RuntimeException e) {
            log.error("악보 수정 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("악보 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 즐겨찾기 토글
     */
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<SheetMusic> toggleFavorite(@PathVariable Long id) {
        try {
            SheetMusic updatedMusic = sheetMusicService.toggleFavorite(id);
            return ResponseEntity.ok(updatedMusic);
        } catch (RuntimeException e) {
            log.error("즐겨찾기 토글 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("즐겨찾기 토글 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 악보 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSheetMusic(@PathVariable Long id) {
        try {
            sheetMusicService.deleteSheetMusic(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("악보 삭제 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("악보 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * 악보 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<Object> getSheetMusicStats() {
        long totalCount = sheetMusicService.getSheetMusicCount();
        
        return ResponseEntity.ok(new Object() {
            public final long total = totalCount;
            
            // Getter for JSON serialization
            public long getTotal() { return total; }
        });
    }
}
