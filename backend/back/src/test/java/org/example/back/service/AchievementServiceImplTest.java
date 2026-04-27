package org.example.back.service;

import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.Achievement;
import org.example.back.entity.UserAchievement;
import org.example.back.exception.AuthenticationException;
import org.example.back.repository.AchievementRepository;
import org.example.back.repository.UserAchievementRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.impl.AchievementServiceImpl;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @InjectMocks
    private AchievementServiceImpl achievementService;

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    @Test
    void getMyAchievement_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> achievementService.getMyAchievement())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void getMyAchievement_shouldBuildOverviewAndResponses() {
        AuthTestUtil.setCurrentUserId(1L);

        Achievement a1 = new Achievement();
        a1.setId(1L);
        a1.setTitle("t1");
        a1.setDescription("d1");
        a1.setIconUrl("i1");
        a1.setConditionValue(10);

        Achievement a2 = new Achievement();
        a2.setId(2L);
        a2.setTitle("t2");
        a2.setDescription("d2");
        a2.setIconUrl("i2");
        a2.setConditionValue(20);

        UserAchievement ua1 = new UserAchievement();
        ua1.setAchievement(a1);
        ua1.setCurrentProgress(10);
        ua1.setIsUnlocked(true);
        ua1.setUnlockedAt(LocalDateTime.of(2026, 1, 1, 0, 0));

        UserAchievement ua2 = new UserAchievement();
        ua2.setAchievement(a2);
        ua2.setCurrentProgress(3);
        ua2.setIsUnlocked(false);

        when(achievementRepository.findAll()).thenReturn(List.of(a1, a2));
        when(userAchievementRepository.findByUserId(1L)).thenReturn(List.of(ua1, ua2));

        UserAchievementOverview overview = achievementService.getMyAchievement();

        assertThat(overview.getTotalCount()).isEqualTo(2);
        assertThat(overview.getUnlockedCount()).isEqualTo(1);
        assertThat(overview.getCompletionRate()).isEqualTo(0.5);
        assertThat(overview.getAchievements()).hasSize(2);
        assertThat(overview.getAchievements().get(0).getId()).isEqualTo(1L);
        assertThat(overview.getAchievements().get(0).getIsUnlocked()).isTrue();
        assertThat(overview.getAchievements().get(1).getId()).isEqualTo(2L);
        assertThat(overview.getAchievements().get(1).getIsUnlocked()).isFalse();
    }
}

