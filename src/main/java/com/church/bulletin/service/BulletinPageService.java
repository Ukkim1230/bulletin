package com.church.bulletin.service;

import com.church.bulletin.entity.BulletinPage;
import com.church.bulletin.repository.BulletinPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BulletinPageService {
    
    private final BulletinPageRepository bulletinPageRepository;
    
    public List<BulletinPage> getAllPages() {
        return bulletinPageRepository.findAllByOrderByPageNumberAsc();
    }
    
    public BulletinPage getPageById(Long id) {
        return bulletinPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("페이지를 찾을 수 없습니다."));
    }
    
    @Transactional
    public BulletinPage createPage(BulletinPage page) {
        return bulletinPageRepository.save(page);
    }
    
    @Transactional
    public BulletinPage updatePage(Long id, BulletinPage updatedPage) {
        BulletinPage page = getPageById(id);
        page.setPageNumber(updatedPage.getPageNumber());
        page.setImageUrl(updatedPage.getImageUrl());
        page.setAlt(updatedPage.getAlt());
        return bulletinPageRepository.save(page);
    }
    
    @Transactional
    public void deletePage(Long id) {
        bulletinPageRepository.deleteById(id);
    }
}
