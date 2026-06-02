package org.example.back.service;

import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.Achievement;
import org.example.back.entity.UserAchievement;

import java.util.List;

public interface AchievementService {

    List<Achievement> listAll();

    Achievement addAchievement(Achievement achievement);

    /**
     * 获取当前用户的成就信息
     */
    UserAchievementOverview getMyAchievement();

    UserAchievementOverview getSomeonesAchievement(long userId);
    
    /**
     * 初始化用户成就记录（幂等操作）
     * 为用户创建所有活跃成就的记录，如果已存在则跳过
     * 适用于用户注册或首次查看成就时
     * @param userId 用户ID
     */
    void initializeUserAchievements(Long userId);

    /**
     * 初始化所有用户的成就记录，用于管理员补齐历史用户
     */
    void initializeAllUsersAchievements();

    /**
     * 重新计算用户所有成就的进度与解锁状态。
     * 用于用户行为发生变化或新增成就定义后同步已有进度。
     */
    void recalculateUserAchievements(Long userId);

    /**
     * 重新计算所有用户的成就数据，用于管理员统一同步历史进度
     */
    void recalculateAllUsersAchievements();

}
