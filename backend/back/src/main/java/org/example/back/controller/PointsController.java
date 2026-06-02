package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.response.PointsHistoryResponse;
import org.example.back.entity.PointsLog;
import org.example.back.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/my/points")
    public ApiResponse<Integer> getPoints() {
        return ApiResponse.success(pointsService.getUserPoints());
    }

    /**
     * 查看个人积分信息
     */
    @GetMapping("/my/history")
    public ApiResponse<PointsHistoryResponse> getMyPointsHistory() {
        return ApiResponse.success(pointsService.getMyPointsHistory());
    }

    /**
     * 管理：获取所有积分日志列表
     */
    @GetMapping("/admin/history")
    public ApiResponse<PointsHistoryResponse> getOnesPointsHistory(@RequestParam long userId) {
        return ApiResponse.success(pointsService.getSomeonesPointsHistory(userId));
    }
}