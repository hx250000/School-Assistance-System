package org.example.back.controller;

import org.example.back.config.SecurityConfig;
import org.example.back.dto.response.PointsHistoryResponse;
import org.example.back.dto.response.UserPointsHistory;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.PointsService;
import org.example.back.testutil.AuthTestUtil;
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
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class PointsControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointsService pointsService;

    private static final Long TEST_USER_ID = 1L;

    @Test
    void info_shouldReturnPoints() throws Exception {
        when(pointsService.getUserPoints()).thenReturn(123);

        mockMvc.perform(get("/api/points/my/points")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(123));
    }

    @Test
    void history_shouldReturnHistoryResponse() throws Exception {
        PointsHistoryResponse response = new PointsHistoryResponse();
        UserPointsHistory history = new UserPointsHistory(10, "签到", "说明", LocalDateTime.now());
        response.setPointsHistoryList(List.of(history));
        response.setIncreasePoints(10);
        response.setDecreasePoints(0);

        when(pointsService.getMyPointsHistory()).thenReturn(response);

        mockMvc.perform(get("/api/points/my/history")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.pointsHistoryList").isArray())
                .andExpect(jsonPath("$.data.increasePoints").value(10))
                .andExpect(jsonPath("$.data.decreasePoints").value(0))
                .andExpect(jsonPath("$.data.pointsHistoryList[0].changeAmount").value(10));
    }
}

