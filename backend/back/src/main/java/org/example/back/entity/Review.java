package org.example.back.entity;

import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Review {
    private Long id;
    private Long taskId;

    private Long fromUserId;
    private Long toUserId;

    private Integer score;
    private String content;

    private LocalDateTime createdAt;
}