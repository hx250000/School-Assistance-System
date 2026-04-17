package org.example.back.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskVO {

    private Long id;
    private String title;
    private String description;
    private Integer needPeople;
    private Integer currentPeople;
    private String type;
    private String status;
    private Long publisherId;

    private Integer rewardPoints;
//    private BigDecimal rewardMoney;

    private Long deadline;
    private Long createdAt;
}