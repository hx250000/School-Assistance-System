package org.example.back.dto.request;

import lombok.Data;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private String type;
    private Integer needPeople;
    private Integer rewardPoints;
    private String deadline;
}