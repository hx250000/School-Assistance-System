package org.example.back.controller;

import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.PointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

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

        mockMvc.perform(get("/api/points/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(123));
    }

    @Test
    void history_shouldReturnArray() throws Exception {
        when(pointsService.getMyPointsHistory()).thenReturn(List.of());

        mockMvc.perform(get("/api/points/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}

