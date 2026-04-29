package org.example.back.dto.response;

import lombok.Data;

@Data
public class TaskVO {

    private Long taskId;
    private String title;
    private String description;
    private Integer needPeople;
    private Integer currentPeople;
    private String type;
    private String status;
    private Long publisherId;
    private String publisherName;

    private Integer rewardPoints;
//    private BigDecimal rewardMoney;

    private Long deadline;
    private Long createdAt;
}