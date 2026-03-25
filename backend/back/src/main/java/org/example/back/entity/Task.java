package org.example.back.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Task {
    private Long id;
    private String title;
    private String description;
    private String type; // LIFE / GAME / STUDY

    private Long publisherId;

    private Integer needPeople;
    private Integer currentPeople;

    private Integer rewardPoints;
    private BigDecimal rewardMoney;

    private String status; // OPEN / IN_PROGRESS / FINISHED / CANCELLED

    private LocalDateTime deadline;
    private LocalDateTime createdAt;
}