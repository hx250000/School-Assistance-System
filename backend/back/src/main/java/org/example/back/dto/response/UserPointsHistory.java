package org.example.back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPointsHistory {
    private Integer changeAmount;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public UserPointsHistory(Integer changeAmount, String title,String description, LocalDateTime createdAt) {
        this.changeAmount = changeAmount;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }
}
