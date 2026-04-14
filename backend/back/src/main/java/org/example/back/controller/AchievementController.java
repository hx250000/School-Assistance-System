package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.UserAchievement;
import org.example.back.repository.AchievementRepository;
import org.example.back.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    @Autowired
    private AchievementService achievementService;

    @GetMapping("/my")
    public ApiResponse<UserAchievementOverview> getMyAchievement() {
        return ApiResponse.success(achievementService.getMyAchievement());
    }
}
