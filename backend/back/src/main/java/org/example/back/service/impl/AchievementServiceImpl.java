package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.dto.response.UserAchievementResponse;
import org.example.back.entity.Achievement;
import org.example.back.entity.UserAchievement;
import org.example.back.exception.AuthenticationException;
import org.example.back.repository.AchievementRepository;
import org.example.back.repository.UserAchievementRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AchievementServiceImpl implements AchievementService {
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    public UserAchievementOverview getMyAchievement() {
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        //获取所有成就定义
        List<Achievement> achievements=achievementRepository.findAll();

        //获取用户所有成就（包含未解锁的）
        List<UserAchievement> myAchievement=userAchievementRepository.findByUserId(userId);

        UserAchievementOverview overview=new UserAchievementOverview();

        List<UserAchievementResponse> userAchievementResponses=new ArrayList<>();

        int unlocked=0;

        for(UserAchievement ua:myAchievement){
            UserAchievementResponse response=new UserAchievementResponse();
            Achievement achievement=ua.getAchievement();

            response.setId(achievement.getId());
            response.setTitle(achievement.getTitle());
            response.setDescription(achievement.getDescription());
            response.setIconUrl(achievement.getIconUrl());
            response.setTotalProgress(achievement.getConditionValue());

            response.setCurrentProgress(ua.getCurrentProgress());
            response.setIsUnlocked(ua.getIsUnlocked());
            response.setUnlockedAt(ua.getUnlockedAt());

            if(ua.getIsUnlocked()){
                unlocked++;
            }

            userAchievementResponses.add(response);

        }

        overview.setUnlockedCount(unlocked);
        overview.setTotalCount(achievements.size());
        overview.setCompletionRate((double) unlocked/achievements.size());
        overview.setAchievements(userAchievementResponses);

        return overview;
    }
}
