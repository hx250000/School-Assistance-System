package org.example.back.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PointsHistoryResponse {
    private List<UserPointsHistory> pointsHistoryList;
    private int increasePoints;
    private int decreasePoints;
}
