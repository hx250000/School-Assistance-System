package org.example.back.service;

import org.example.back.dto.response.UserPointsHistory;
import org.example.back.entity.PointsLog;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.PointsLogRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.impl.PointsServiceImpl;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointsLogRepository pointsLogRepository;

    @InjectMocks
    private PointsServiceImpl pointsService;

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    @Test
    void getUserPoints_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> pointsService.getUserPoints())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void getUserPoints_whenUserNotFound_shouldThrowResourceNotFoundException() {
        AuthTestUtil.setCurrentUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointsService.getUserPoints())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户 1 未找到");
    }

    @Test
    void getUserPoints_whenSuccess_shouldReturnPoints() {
        AuthTestUtil.setCurrentUserId(1L);
        User u = new User();
        u.setId(1L);
        u.setPoints(123);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        Integer pts = pointsService.getUserPoints();

        assertThat(pts).isEqualTo(123);
    }

    @Test
    void addPoints_shouldUpdateUserPoints_andWriteLog() {
        User u = new User();
        u.setId(2L);
        u.setPoints(10);
        when(userRepository.findById(2L)).thenReturn(Optional.of(u));

        pointsService.addPoints(2L, 5, "t", "d");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPoints()).isEqualTo(15);

        ArgumentCaptor<PointsLog> logCaptor = ArgumentCaptor.forClass(PointsLog.class);
        verify(pointsLogRepository).save(logCaptor.capture());
        PointsLog log = logCaptor.getValue();
        assertThat(log.getUserId()).isEqualTo(2L);
        assertThat(log.getChangeAmount()).isEqualTo(5);
        assertThat(log.getTitle()).isEqualTo("t");
        assertThat(log.getDescription()).isEqualTo("d");
    }

    @Test
    void getMyPointsHistory_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> pointsService.getMyPointsHistory())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void getMyPointsHistory_shouldMapLogsToHistoryDTOs() {
        AuthTestUtil.setCurrentUserId(3L);

        PointsLog l1 = new PointsLog();
        l1.setId(1L);
        l1.setUserId(3L);
        l1.setChangeAmount(10);
        l1.setTitle("a");
        l1.setDescription("da");
        l1.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));

        PointsLog l2 = new PointsLog();
        l2.setId(2L);
        l2.setUserId(3L);
        l2.setChangeAmount(-5);
        l2.setTitle("b");
        l2.setDescription("db");
        l2.setCreatedAt(LocalDateTime.of(2026, 1, 2, 0, 0));

        when(pointsLogRepository.findByUserIdOrderByCreatedAtDesc(3L)).thenReturn(List.of(l2, l1));

        List<UserPointsHistory> list = pointsService.getMyPointsHistory();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getId()).isEqualTo(2L);
        assertThat(list.get(0).getChangeAmount()).isEqualTo(-5);
        assertThat(list.get(0).getTitle()).isEqualTo("b");
        assertThat(list.get(1).getId()).isEqualTo(1L);
    }
}

