package org.example.back.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ReviewCreateRequest {
    private Long taskId;

    private Long fromUserId;

    private Long toUserId;

    private Integer score;

    private String content;

}
