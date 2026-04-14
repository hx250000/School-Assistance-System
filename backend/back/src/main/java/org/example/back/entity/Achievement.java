package org.example.back.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "achievements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;  // 成就标题，如"任务达人"

    @Column(columnDefinition = "TEXT")
    private String description;  // 成就描述，如"完成10个任务"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementTYPE type;  // 成就类型枚举

    @Column(nullable = false)
    private Integer conditionValue;  // 达成条件值，如10（完成10个任务）

    @Column(length = 255)
    private String iconUrl;  // 成就图标URL

    @Column(nullable = false)
    private Integer rewardPoints;  // 解锁奖励积分

    @Column(nullable = false)
    private Boolean isActive = true;  // 是否启用

//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
}

// 成就类型枚举
