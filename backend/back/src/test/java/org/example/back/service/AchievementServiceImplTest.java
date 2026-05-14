package org.example.back.service;

import org.example.back.dto.response.UserAchievementOverview;
import org.example.back.entity.Achievement;
import org.example.back.entity.AchievementTYPE;
import org.example.back.entity.User;
import org.example.back.entity.UserAchievement;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.*;
import org.example.back.service.impl.AchievementServiceImpl;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAchievementRepository userAchievementRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskParticipantRepository taskParticipantRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private LoginRecordRepository loginRecordRepository;

    @Mock
    private PointsService pointsService;

    @InjectMocks
    private AchievementServiceImpl achievementService;

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    @Test
    void listAll_shouldReturnAllAchievements() {
        Achievement a1 = new Achievement();
        a1.setId(1L);
        a1.setTitle("Task Master");

        Achievement a2 = new Achievement();
        a2.setId(2L);
        a2.setTitle("Point Collector");

        when(achievementRepository.findAll()).thenReturn(List.of(a1, a2));

        List<Achievement> result = achievementService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Task Master");
        assertThat(result.get(1).getTitle()).isEqualTo("Point Collector");
    }

    @Test
    void addAchievement_shouldSaveAndReturnAchievement() {
        Achievement achievement = new Achievement();
        achievement.setTitle("New Achievement");

        Achievement savedAchievement = new Achievement();
        savedAchievement.setId(1L);
        savedAchievement.setTitle("New Achievement");

        when(achievementRepository.save(any(Achievement.class))).thenReturn(savedAchievement);

        Achievement result = achievementService.addAchievement(achievement);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(achievementRepository, times(1)).save(achievement);
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

        User user = new User();
        user.setId(1L);
        user.setPoints(15); // 设置为 15，小于第二个成就的 20 条件

        Achievement a1 = new Achievement();
        a1.setId(1L);
        a1.setTitle("t1");
        a1.setDescription("d1");
        a1.setIconUrl("i1");
        a1.setConditionValue(10);
        a1.setType(AchievementTYPE.TASK_COMPLETED);

        Achievement a2 = new Achievement();
        a2.setId(2L);
        a2.setTitle("t2");
        a2.setDescription("d2");
        a2.setIconUrl("i2");
        a2.setConditionValue(20);
        a2.setType(AchievementTYPE.POINTS_EARNED);

        UserAchievement ua1 = new UserAchievement();
        ua1.setAchievement(a1);
        ua1.setCurrentProgress(10);
        ua1.setIsUnlocked(true);
        ua1.setUnlockedAt(LocalDateTime.of(2026, 1, 1, 0, 0));

        UserAchievement ua2 = new UserAchievement();
        ua2.setAchievement(a2);
        ua2.setCurrentProgress(15);
        ua2.setIsUnlocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(achievementRepository.findAll()).thenReturn(List.of(a1, a2));
        when(userAchievementRepository.findByUserId(1L)).thenReturn(List.of(ua1, ua2));
        when(achievementRepository.findByIsActiveTrue()).thenReturn(List.of(a1, a2));
        when(taskParticipantRepository.countByUserIdAndStatus(anyLong(), anyString())).thenReturn(10L);
        when(taskRepository.countByPublisherId(anyLong())).thenReturn(0L);
        when(reviewRepository.countByToUserIdAndScoreGreaterThanEqual(anyLong(), anyInt())).thenReturn(0L);
        when(loginRecordRepository.findByUserIdOrderByLoginDateDesc(anyLong())).thenReturn(List.of());

        UserAchievementOverview overview = achievementService.getMyAchievement();

        assertThat(overview.getTotalCount()).isEqualTo(2);
        assertThat(overview.getUnlockedCount()).isEqualTo(1);
        assertThat(overview.getCompletionRate()).isEqualTo(0.5);
        assertThat(overview.getAchievements()).hasSize(2);
    }

    @Test
    void initializeUserAchievements_shouldCreateMissingRecords() {
        User user = new User();
        user.setId(1L);

        Achievement a1 = new Achievement();
        a1.setId(1L);
        a1.setIsActive(true);

        Achievement a2 = new Achievement();
        a2.setId(2L);
        a2.setIsActive(true);

        UserAchievement existingUa = new UserAchievement();
        existingUa.setAchievement(a1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(achievementRepository.findByIsActiveTrue()).thenReturn(List.of(a1, a2));
        when(userAchievementRepository.findByUserId(1L)).thenReturn(List.of(existingUa));

        achievementService.initializeUserAchievements(1L);

        verify(userAchievementRepository, times(1)).saveAll(anyList());
    }

    @Test
    void initializeUserAchievements_whenUserNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> achievementService.initializeUserAchievements(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void recalculateUserAchievements_shouldUpdateProgressAndUnlock() {
        User user = new User();
        user.setId(1L);
        user.setPoints(200);

        Achievement achievement = new Achievement();
        achievement.setId(1L);
        achievement.setType(AchievementTYPE.POINTS_EARNED);
        achievement.setConditionValue(100);

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setId(1L);
        userAchievement.setAchievement(achievement);
        userAchievement.setCurrentProgress(0);
        userAchievement.setIsUnlocked(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(achievementRepository.findByIsActiveTrue()).thenReturn(List.of(achievement));
        when(userAchievementRepository.findByUserId(1L)).thenReturn(List.of(userAchievement));
        when(taskParticipantRepository.countByUserIdAndStatus(anyLong(), anyString())).thenReturn(0L);
        when(taskRepository.countByPublisherId(anyLong())).thenReturn(0L);
        when(reviewRepository.countByToUserIdAndScoreGreaterThanEqual(anyLong(), anyInt())).thenReturn(0L);
        when(loginRecordRepository.findByUserIdOrderByLoginDateDesc(anyLong())).thenReturn(List.of());

        achievementService.recalculateUserAchievements(1L);

        verify(userAchievementRepository, times(1)).saveAll(anyList());
    }

    @Test
    void recalculateUserAchievements_whenUserNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> achievementService.recalculateUserAchievements(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void initializeAllUsersAchievements_shouldProcessAllUsers() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(achievementRepository.findByIsActiveTrue()).thenReturn(List.of());

        achievementService.initializeAllUsersAchievements();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void recalculateAllUsersAchievements_shouldProcessAllUsers() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(achievementRepository.findByIsActiveTrue()).thenReturn(List.of());
        when(userAchievementRepository.findByUserId(anyLong())).thenReturn(List.of());

        achievementService.recalculateAllUsersAchievements();

        verify(userRepository, times(1)).findAll();
    }
}

