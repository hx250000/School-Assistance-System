package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.Achievement;
import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.entity.User;
import org.example.back.entity.UserAchievement;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.AchievementRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    @Autowired
    private AchievementService achievementService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/my")
    public ApiResponse<UserAchievementOverview> getMyAchievement() {
        return ApiResponse.success(achievementService.getMyAchievement());
    }
    
    /**
     * 管理接口：为所有没有成就记录的用户初始化成就
     * 适用于新增成就定义后，需要为既有用户补齐记录的场景
     * 建议：在实际系统中加上权限验证，确保只有管理员可以调用
     * @return 初始化的用户数
     */
    @PostMapping("/admin/init")
    public ApiResponse<Integer> initializeAllUsers() {
        requireAdmin();
        achievementService.initializeAllUsersAchievements();
        int userCount = userRepository.findAll().size();
        return ApiResponse.success(userCount);
    }

    @PostMapping("/admin/recalculate")
    public ApiResponse<Integer> recalculateAllUsers() {
        requireAdmin();
        achievementService.recalculateAllUsersAchievements();
        int userCount = userRepository.findAll().size();
        return ApiResponse.success(userCount);
    }

    @PostMapping("/admin/achievement")
    public ApiResponse<Achievement> addAchievement(@RequestBody Achievement achievement) {
        requireAdmin();
        return ApiResponse.success(achievementService.addAchievement(achievement));
    }

    @GetMapping("/admin/list/achievements")
    public ApiResponse<List<Achievement>> listAchievements() {
        requireAdmin();
        return ApiResponse.success(achievementService.listAll());
    }

    @GetMapping("/admin/list/userachievements")
    public ApiResponse<UserAchievementOverview> listUserAchievements(@RequestParam long userId) {
        requireAdmin();
        return ApiResponse.success(achievementService.getSomeonesAchievement(userId));
    }

    private void requireAdmin() {
        Long currentUserId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthenticationException("用户未登录");
        }
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("当前用户不存在"));
        if (currentUser.getAdmin() == null || !currentUser.getAdmin()) {
            throw new AuthenticationException("仅管理员可以执行此操作");
        }
    }
}
