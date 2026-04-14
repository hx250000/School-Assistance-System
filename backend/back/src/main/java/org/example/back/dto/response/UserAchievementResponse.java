package org.example.back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAchievementResponse {
    private Long id;
    private String title;
    private String description;
    private String iconUrl;
    private Integer currentProgress;  // 用户当前进度
    private Integer totalProgress;    // 目标进度
    private Boolean isUnlocked;       // 是否解锁
    private LocalDateTime unlockedAt; // 解锁时间
}
