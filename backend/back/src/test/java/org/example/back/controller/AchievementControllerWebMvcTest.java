package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.config.SecurityConfig;
import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.Achievement;
import org.example.back.entity.AchievementTYPE;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AchievementController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class AchievementControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AchievementService achievementService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getMyAchievements_shouldReturnOverview() throws Exception {
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

    @Test
    void initializeAllUsers_shouldCallServiceAndReturnUserCount() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(new org.example.back.entity.User(), new org.example.back.entity.User()));

        mockMvc.perform(post("/api/achievements/admin/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));

        verify(achievementService, times(1)).initializeAllUsersAchievements();
    }

    @Test
    void recalculateAllUsers_shouldCallServiceAndReturnUserCount() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(new org.example.back.entity.User(), new org.example.back.entity.User()));

        mockMvc.perform(post("/api/achievements/admin/recalculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));

        verify(achievementService, times(1)).recalculateAllUsersAchievements();
    }

    @Test
    void addAchievement_shouldReturnSavedAchievement() throws Exception {
        Achievement achievement = new Achievement();
        achievement.setTitle("Test Achievement");
        achievement.setDescription("Test Description");
        achievement.setType(AchievementTYPE.TASK_COMPLETED);
        achievement.setConditionValue(10);
        achievement.setRewardPoints(100);
        achievement.setIsActive(true);

        Achievement savedAchievement = new Achievement();
        savedAchievement.setId(1L);
        savedAchievement.setTitle("Test Achievement");
        savedAchievement.setDescription("Test Description");
        savedAchievement.setType(AchievementTYPE.TASK_COMPLETED);
        savedAchievement.setConditionValue(10);
        savedAchievement.setRewardPoints(100);
        savedAchievement.setIsActive(true);

        when(achievementService.addAchievement(any(Achievement.class))).thenReturn(savedAchievement);

        mockMvc.perform(post("/api/achievements/admin/achievement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(achievement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Achievement"));
    }

    @Test
    void listAchievements_shouldReturnAllAchievements() throws Exception {
        Achievement a1 = new Achievement();
        a1.setId(1L);
        a1.setTitle("Achievement 1");

        Achievement a2 = new Achievement();
        a2.setId(2L);
        a2.setTitle("Achievement 2");

        when(achievementService.listAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/achievements/admin/list/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("Achievement 1"));
    }

    @Test
    void listUserAchievements_shouldReturnUserAchievements() throws Exception {
        UserAchievementOverview overview = new UserAchievementOverview();
        overview.setTotalCount(5);
        overview.setUnlockedCount(3);
        overview.setCompletionRate(0.6);

        when(achievementService.getSomeonesAchievement(1L)).thenReturn(overview);

        mockMvc.perform(get("/api/achievements/admin/list/userachievements")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCount").value(5))
                .andExpect(jsonPath("$.data.unlockedCount").value(3))
                .andExpect(jsonPath("$.data.completionRate").value(0.6));
    }
}

