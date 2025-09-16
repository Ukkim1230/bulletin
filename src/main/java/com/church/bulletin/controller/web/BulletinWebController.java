package com.church.bulletin.controller.web;

import com.church.bulletin.service.BulletinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BulletinWebController {
    
    private final BulletinService bulletinService;
    
    @GetMapping("/")
    public String home(Model model) {
        log.info("메인 페이지 요청");
        BulletinService.BulletinData bulletin = bulletinService.getTodayBulletin();
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("currentDate", LocalDate.now());
        return "index";
    }
    
    @GetMapping("/bulletin")
    public String bulletin(Model model, 
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        log.info("주보 페이지 요청 - 날짜: {}", date);
        
        BulletinService.BulletinData bulletin = bulletinService.getBulletinByDate(date);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("selectedDate", date);
        model.addAttribute("currentDate", LocalDate.now());
        return "bulletin";
    }
    
    @GetMapping("/bulletin/{date}")
    public String bulletinByDate(Model model, 
                                @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("특정 날짜 주보 페이지 요청 - 날짜: {}", date);
        
        BulletinService.BulletinData bulletin = bulletinService.getBulletinByDate(date);
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("selectedDate", date);
        model.addAttribute("currentDate", LocalDate.now());
        return "bulletin";
    }
    
    @GetMapping("/mobile")
    public String mobile(Model model) {
        log.info("모바일 주보 페이지 요청");
        BulletinService.BulletinData bulletin = bulletinService.getTodayBulletin();
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("currentDate", LocalDate.now());
        return "mobile";
    }
}
