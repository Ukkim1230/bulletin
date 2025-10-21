package com.church.bulletin.service;

import com.church.bulletin.entity.Announcement;
import com.church.bulletin.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    
    /**
     * 모든 활성 공지사항 조회
     */
    @Transactional(readOnly = true)
    public List<Announcement> getAllActiveAnnouncements() {
        LocalDate today = LocalDate.now();
        return announcementRepository.findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByDisplayOrderAscCreatedAtDesc(
                today, today);
    }
    
    /**
     * ID로 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }
    
    /**
     * 공지사항 생성
     */
    public Announcement createAnnouncement(Announcement announcement) {
        // 기본값 설정
        if (announcement.getIsActive() == null) {
            announcement.setIsActive(true);
        }
        if (announcement.getIsImportant() == null) {
            announcement.setIsImportant(false);
        }
        if (announcement.getDisplayOrder() == null) {
            announcement.setDisplayOrder(0);
        }
        
        return announcementRepository.save(announcement);
    }
    
    /**
     * 공지사항 수정
     */
    public Announcement updateAnnouncement(Announcement announcement) {
        Optional<Announcement> existingOpt = announcementRepository.findById(announcement.getId());
        if (existingOpt.isEmpty()) {
            return null;
        }
        
        Announcement existing = existingOpt.get();
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setAnnouncementDate(announcement.getAnnouncementDate());
        existing.setIsImportant(announcement.getIsImportant());
        existing.setDisplayOrder(announcement.getDisplayOrder());
        existing.setStartDate(announcement.getStartDate());
        existing.setEndDate(announcement.getEndDate());
        
        return announcementRepository.save(existing);
    }
    
    /**
     * 공지사항 삭제
     */
    public boolean deleteAnnouncement(Long id) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isEmpty()) {
            return false;
        }
        
        announcementRepository.delete(announcementOpt.get());
        return true;
    }
}
