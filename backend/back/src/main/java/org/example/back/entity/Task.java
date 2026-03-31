package org.example.back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "任务实体")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "任务ID")
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "任务标题")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "任务描述")
    private String description;

    @Column(nullable = false, length = 20)
    @Schema(description = "任务类型", example = "LIFE/GAME/STUDY")
    private String type;

    @Column(name = "publisher_id", nullable = false)
    @Schema(description = "发布人ID")
    private Long publisherId;

    @Column(name = "need_people", nullable = false)
    @Schema(description = "需要人数")
    private Integer needPeople;

    @Column(name = "current_people", nullable = false)
    @Schema(description = "当前已加入人数")
    private Integer currentPeople;

    @Column(name = "reward_points")
    @Schema(description = "奖励积分")
    private Integer rewardPoints;

    @Column(name = "reward_money", precision = 10, scale = 2)
    @Schema(description = "奖励金额")
    private BigDecimal rewardMoney;

    @Column(nullable = false, length = 20)
    @Schema(description = "任务状态", example = "OPEN/IN_PROGRESS/FINISHED/CANCELLED")
    private String status;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.currentPeople == null) {
            this.currentPeople = 0;
        }
        if (this.status == null) {
            this.status = "OPEN";
        }
    }
}