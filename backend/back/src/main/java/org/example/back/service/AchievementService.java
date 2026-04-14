package org.example.back.service;

import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.UserAchievement;

import java.util.List;

public interface AchievementService {
    UserAchievementOverview getMyAchievement();
}
