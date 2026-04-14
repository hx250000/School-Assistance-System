package org.example.back.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class UserAchievementOverview {
    private Integer unlockedCount;    // 已解锁数量
    private Integer totalCount;       // 总数量
    private Double completionRate;    // 完成率
    private List<UserAchievementResponse> achievements; // 成就列表

}
