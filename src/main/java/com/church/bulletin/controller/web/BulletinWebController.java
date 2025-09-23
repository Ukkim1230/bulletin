package com.church.bulletin.controller.web;

import com.church.bulletin.service.BulletinService;
import com.church.bulletin.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BulletinWebController {
    
    private final BulletinService bulletinService;
    private final QRCodeService qrCodeService;
    
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;
    
    @GetMapping("/")
    public String home(Model model) {
        log.info("메인 페이지 요청 - 모바일 주보 표시");
        BulletinService.BulletinData bulletin = bulletinService.getTodayBulletin();
        model.addAttribute("bulletin", bulletin);
        model.addAttribute("currentDate", LocalDate.now());
        
        // QR 코드 생성 (현재 URL)
        String qrCodeImage = qrCodeService.generateQRCodeImage(appUrl, 250, 250);
        model.addAttribute("qrCodeImage", qrCodeImage);
        model.addAttribute("appUrl", appUrl);
        
        return "mobile";
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
    
    /**
     * QR 코드 생성 API
     * @param url QR 코드에 포함할 URL
     * @return Base64로 인코딩된 QR 코드 이미지
     */
    @GetMapping(value = "/qr", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String generateQRCode(@RequestParam(defaultValue = "") String url) {
        if (url.isEmpty()) {
            url = appUrl;
        }
        log.info("QR 코드 생성 요청 - URL: {}", url);
        return qrCodeService.generateQRCodeImage(url, 250, 250);
    }
    
}
