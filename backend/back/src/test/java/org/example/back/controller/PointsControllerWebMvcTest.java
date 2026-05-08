package org.example.back.controller;

import org.example.back.dto.response.PointsHistoryResponse;
import org.example.back.dto.response.UserPointsHistory;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.PointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointsController.class)
@Import(GlobalExceptionHandler.class)
class PointsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointsService pointsService;

    @Test
    void info_shouldReturnPoints() throws Exception {
        when(pointsService.getUserPoints()).thenReturn(123);

        mockMvc.perform(get("/api/points/mypoints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(123));
    }

    @Test
    void history_shouldReturnHistoryResponse() throws Exception {
        // 1. 准备 Mock 返回对象
        PointsHistoryResponse response = new PointsHistoryResponse();
        UserPointsHistory history = new UserPointsHistory(10, "签到", "说明", LocalDateTime.now());
        response.setPointsHistoryList(List.of(history));
        response.setIncreasePoints(10);
        response.setDecreasePoints(0);

        // 2. 配置 Mock 行为
        when(pointsService.getMyPointsHistory()).thenReturn(response);

        // 3. 执行请求并验证
        mockMvc.perform(get("/api/points/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // 注意：现在的 $.data 是一个对象，包含 pointsHistoryList 数组
                .andExpect(jsonPath("$.data.pointsHistoryList").isArray())
                .andExpect(jsonPath("$.data.increasePoints").value(10))
                .andExpect(jsonPath("$.data.decreasePoints").value(0))
                .andExpect(jsonPath("$.data.pointsHistoryList[0].changeAmount").value(10));
    }
}

