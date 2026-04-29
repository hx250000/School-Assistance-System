package org.example.back.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class UserPointsHistory {
    private Integer changeAmount;
    private String title;
    private String description;
    private String time;

    public UserPointsHistory(Integer changeAmount, String title,String description, LocalDateTime createdAt) {
        this.changeAmount = changeAmount;
        this.title = title;
        this.description = description;
        if (createdAt != null) {
            this.time = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    public void setTime(LocalDateTime time) {
        this.time = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
