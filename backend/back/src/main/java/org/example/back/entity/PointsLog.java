package org.example.back.entity;

import java.time.LocalDateTime;
import lombok.Data;
@Data
public class PointsLog {
    private Long id;
    private Long userId;

    private Integer changeAmount; // +20 / -50
    private String reason;        // 完成任务 / 兑换商品

    private LocalDateTime createdAt;
}