package com.church.bulletin.controller.web;

import com.church.bulletin.entity.SmallGroup;
import com.church.bulletin.service.SmallGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/small-groups")
@RequiredArgsConstructor
@Slf4j
public class SmallGroupWebController {
    
    private final SmallGroupService smallGroupService;
    
    /**
     * 순모임 목록 페이지
     */
    @GetMapping
    public String smallGroupsPage(Model model) {
        try {
            List<SmallGroup> smallGroups = smallGroupService.getAllActiveSmallGroups();
            model.addAttribute("smallGroups", smallGroups);
            log.info("순모임 목록 페이지 요청 - {}개 순모임", smallGroups.size());
            return "small-groups/list";
        } catch (Exception e) {
            log.error("순모임 목록 페이지 로드 실패", e);
            return "error";
        }
    }
}
