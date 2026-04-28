package org.example.back.service;

import org.example.back.dto.response.PointsHistoryResponse;
import org.example.back.dto.response.UserPointsHistory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PointsService {

    Integer getUserPoints();

    void addPoints(Long userId, Integer points,String title,String desc);

    //添加积分记录查询功能
    PointsHistoryResponse getMyPointsHistory();
}