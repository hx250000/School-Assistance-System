package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.dto.response.UserAchievementResponse;
import org.example.back.entity.Achievement;
import org.example.back.entity.AchievementTYPE;
import org.example.back.entity.User;
import org.example.back.entity.UserAchievement;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.AchievementRepository;
import org.example.back.repository.LoginRecordRepository;
import org.example.back.repository.ReviewRepository;
import org.example.back.repository.TaskParticipantRepository;
import org.example.back.repository.TaskRepository;
import org.example.back.repository.UserAchievementRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.example.back.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class AchievementServiceImpl implements AchievementService {
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskParticipantRepository taskParticipantRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @Autowired
    private PointsService pointsService;

    /**
     * 列举出所有成就项
     */
    @Override
    public List<Achievement> listAll(){
        return achievementRepository.findAll();
    }

    /**
     * 添加单个成就项
     */
    @Override
    @Transactional
    public Achievement addAchievement(Achievement achievement) {
        Achievement saved = achievementRepository.save(achievement);
        // 新增成就后，主动为历史用户补齐记录并同步进度
        initializeAllUsersAchievements();
        recalculateAllUsersAchievements();
        return saved;
    }

    /**
     * 初始化所有用户的成就记录，用于管理员补齐历史用户
     */
    @Override
    @Transactional
    public void initializeAllUsersAchievements() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            initializeUserAchievements(user.getId());
        }
    }

    /**
     * 重新计算所有用户的成就数据，用于管理员统一同步历史进度
     */
    @Override
    @Transactional
    public void recalculateAllUsersAchievements() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            recalculateUserAchievements(user.getId());
        }
    }

    /**
     * 重新计算用户所有成就进度与解锁状态。
     */
    @Override
    @Transactional
    public void recalculateUserAchievements(Long userId) {
        ensureUserAchievementsExist(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户 " + userId + " 不存在"));

        int completedTasks = (int) taskParticipantRepository.countByUserIdAndStatus(userId, "FINISHED");
        int publishedTasks = (int) taskRepository.countByPublisherId(userId);
        int totalPoints = user.getPoints() != null ? user.getPoints() : 0;
        int highRatingCount = (int) reviewRepository.countByToUserIdAndScoreGreaterThanEqual(userId, 4);
        int consecutiveDays = countConsecutiveLoginDays(userId);
        int helpOthersCount = completedTasks;

        List<UserAchievement> records = userAchievementRepository.findByUserId(userId);
        List<UserAchievement> updates = new ArrayList<>();

        for (UserAchievement record : records) {
            Achievement achievement = record.getAchievement();
            int actualValue = calculateActualValue(achievement.getType(), completedTasks, publishedTasks, totalPoints, highRatingCount, consecutiveDays, helpOthersCount);
            int progress = Math.min(actualValue, achievement.getConditionValue());
            record.setCurrentProgress(progress);

            if (!record.getIsUnlocked() && actualValue >= achievement.getConditionValue()) {
                record.setIsUnlocked(true);
                record.setUnlockedAt(java.time.LocalDateTime.now());
                pointsService.addPoints(userId,achievement.getRewardPoints(),"完成成就",achievement.getDescription());
            }
            updates.add(record);
        }

        if (!updates.isEmpty()) {
            userAchievementRepository.saveAll(updates);
        }
    }

    private int calculateActualValue(AchievementTYPE achievementType,
                                     int completedTasks,
                                     int publishedTasks,
                                     int totalPoints,
                                     int highRatingCount,
                                     int consecutiveDays,
                                     int helpOthersCount) {
        switch (achievementType) {
            case TASK_COMPLETED:
                return completedTasks;
            case TASK_PUBLISHED:
                return publishedTasks;
            case POINTS_EARNED:
                return totalPoints;
            case HIGH_RATING:
                return highRatingCount;
            case CONSECUTIVE_DAYS:
                return consecutiveDays;
            case HELP_OTHERS:
                return helpOthersCount;
            default:
                return 0;
        }
    }

    private int countConsecutiveLoginDays(Long userId) {
        List<org.example.back.entity.LoginRecord> records = loginRecordRepository.findByUserIdOrderByLoginDateDesc(userId);
        if (records.isEmpty()) {
            return 0;
        }

        int days = 0;
        java.time.LocalDate expected = java.time.LocalDate.now();
        for (org.example.back.entity.LoginRecord record : records) {
            java.time.LocalDate loginDate = record.getLoginDate();
            if (loginDate.isEqual(expected)) {
                days++;
                expected = expected.minusDays(1);
            } else if (loginDate.isBefore(expected)) {
                break;
            }
        }
        return days;
    }

    /**
     * 获取当前用户的成就概览
     * 包含已解锁和未解锁的成就，以及当前进度
     */
    @Override
    public UserAchievementOverview getMyAchievement() {
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        return getUserAchievement(userId);
    }

    @Override
    public UserAchievementOverview getSomeonesAchievement(long userId) {
        return getUserAchievement(userId);
    }

    public UserAchievementOverview getUserAchievement(Long userId) {
        // 重新计算当前用户成就进度，保证新增成就项也能同步历史记录
        recalculateUserAchievements(userId);

        // 获取所有成就定义
        List<Achievement> achievements = achievementRepository.findAll();

        // 获取用户所有成就（包含未解锁的）
        List<UserAchievement> myAchievement = userAchievementRepository.findByUserId(userId);

        UserAchievementOverview overview = new UserAchievementOverview();

        List<UserAchievementResponse> userAchievementResponses = new ArrayList<>();

        int unlocked = 0;

        for (UserAchievement ua : myAchievement) {
            UserAchievementResponse response = new UserAchievementResponse();
            Achievement achievement = ua.getAchievement();

            response.setId(achievement.getId());
            response.setTitle(achievement.getTitle());
            response.setDescription(achievement.getDescription());
            response.setIconUrl(achievement.getIconUrl());
            response.setTotalProgress(achievement.getConditionValue());

            response.setCurrentProgress(ua.getCurrentProgress());
            response.setIsUnlocked(ua.getIsUnlocked());
            //response.setUnlockedAt(ua.getUnlockedAt().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
            if (ua.getIsUnlocked() && ua.getUnlockedAt() != null) {
                // 只有解锁了且时间不为空才转换
                response.setUnlockedAt(ua.getUnlockedAt().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
                unlocked++;
            } else {
                // 未解锁时设为 null
                response.setUnlockedAt(null);
            }

            userAchievementResponses.add(response);
        }

        overview.setUnlockedCount(unlocked);
        overview.setTotalCount(achievements.size());
        overview.setCompletionRate(achievements.size() > 0 ? (double) unlocked / achievements.size() : 0);
        overview.setAchievements(userAchievementResponses);

        return overview;
    }

    /**
     * 初始化用户成就记录（幂等操作）
     * 为用户创建所有活跃成就的记录，如果已存在则跳过
     * 适用于用户注册或首次查看成就时
     * 
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void initializeUserAchievements(Long userId) {
        // 获取用户对象
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户 " + userId + " 不存在"));

        // 获取所有活跃的成就定义
        List<Achievement> achievements = achievementRepository.findByIsActiveTrue();

        // 获取用户已有的成就记录
        List<UserAchievement> existingAchievements = userAchievementRepository.findByUserId(userId);

        // 转换成 achievementId 的集合，用于快速查找
        java.util.Set<Long> existingAchievementIds = new java.util.HashSet<>();
        for (UserAchievement ua : existingAchievements) {
            existingAchievementIds.add(ua.getAchievement().getId());
        }

        // 为缺失的成就创建记录
        List<UserAchievement> newRecords = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (!existingAchievementIds.contains(achievement.getId())) {
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setUser(user);
                userAchievement.setAchievement(achievement);
                userAchievement.setCurrentProgress(0);
                userAchievement.setIsUnlocked(false);
                userAchievement.setUnlockedAt(null);
                // createdAt 和 updatedAt 由 @PrePersist 自动设置
                newRecords.add(userAchievement);
            }
        }

        // 批量保存新记录
        if (!newRecords.isEmpty()) {
            userAchievementRepository.saveAll(newRecords);
        }
    }

    /**
     * 确保用户的成就记录存在
     * 如果不存在，则自动初始化（懒初始化）
     * 
     * @param userId 用户ID
     */
    private void ensureUserAchievementsExist(Long userId) {
        // 检查用户是否有任何成就记录
        List<UserAchievement> userAchievements = userAchievementRepository.findByUserId(userId);

        // 获取所有活跃成就数量
        List<Achievement> activeAchievements = achievementRepository.findByIsActiveTrue();

        // 如果成就记录数少于活跃成就数，则进行初始化
        if (userAchievements.size() < activeAchievements.size()) {
            initializeUserAchievements(userId);
        }
    }

    @Override
    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementRepository.findByUserId(userId);
    }
}
