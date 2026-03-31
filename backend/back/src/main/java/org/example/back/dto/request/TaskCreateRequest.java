package org.example.back.dto.request;
import lombok.Data;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private String type;
    private Long publisherId;      // ✅ 补上
    private Integer needPeople;
    private Integer rewardPoints;
    private Double rewardMoney;    // ✅ 补上
    private String deadline;
}