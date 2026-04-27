package org.example.back.controller;

import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.service.AchievementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AchievementController.class)
@Import(GlobalExceptionHandler.class)
class AchievementControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AchievementService achievementService;

    @Test
    void my_shouldReturnOverview() throws Exception {
        UserAchievementOverview ov = new UserAchievementOverview();
        ov.setTotalCount(2);
        ov.setUnlockedCount(1);
        ov.setCompletionRate(0.5);
        when(achievementService.getMyAchievement()).thenReturn(ov);

        mockMvc.perform(get("/api/achievements/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCount").value(2))
                .andExpect(jsonPath("$.data.unlockedCount").value(1));
    }
}

