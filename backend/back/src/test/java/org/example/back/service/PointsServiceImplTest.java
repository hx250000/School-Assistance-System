package org.example.back.service;

import org.example.back.dto.response.PointsHistoryResponse;
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
        // 1. 模拟当前登录用户
        AuthTestUtil.setCurrentUserId(3L);

        // 2. 准备模拟数据（注意：UserPointsHistory 构造函数会将 LocalDateTime 转为 String）
        LocalDateTime time1 = LocalDateTime.of(2026, 1, 1, 10, 30);
        PointsLog l1 = new PointsLog();
        l1.setId(1L);
        l1.setUserId(3L);
        l1.setChangeAmount(10);
        l1.setTitle("签到奖励");
        l1.setDescription("每日签到获得积分");
        l1.setCreatedAt(time1);

        LocalDateTime time2 = LocalDateTime.of(2026, 1, 2, 15, 45);
        PointsLog l2 = new PointsLog();
        l2.setId(2L);
        l2.setUserId(3L);
        l2.setChangeAmount(-5);
        l2.setTitle("兑换商品");
        l2.setDescription("消耗积分兑换挂件");
        l2.setCreatedAt(time2);

        // 模拟仓库返回：按时间降序（l2 在前，l1 在后）[cite: 1, 2]
        when(pointsLogRepository.findByUserIdOrderByCreatedAtDesc(3L)).thenReturn(List.of(l2, l1));

        // 3. 执行被测方法[cite: 2]
        PointsHistoryResponse response = pointsService.getMyPointsHistory();

        // 4. 验证返回的统计数据
        assertThat(response).isNotNull();
        assertThat(response.getIncreasePoints()).isEqualTo(10); // 10[cite: 2, 3]
        assertThat(response.getDecreasePoints()).isEqualTo(-5); // -5[cite: 2, 3]

        // 5. 验证明细列表内容[cite: 3, 4]
        List<UserPointsHistory> historyList = response.getPointsHistoryList();
        assertThat(historyList).hasSize(2);

        // 验证第一条记录（l2：减少5分）
        UserPointsHistory h1 = historyList.get(0);
        assertThat(h1.getChangeAmount()).isEqualTo(-5);
        assertThat(h1.getTitle()).isEqualTo("兑换商品");
        // 验证时间格式化是否正确（yyyy-MM-dd HH:mm）
        assertThat(h1.getTime()).isEqualTo("2026-01-02 15:45");

        // 验证第二条记录（l1：增加10分）
        UserPointsHistory h2 = historyList.get(1);
        assertThat(h2.getChangeAmount()).isEqualTo(10);
        assertThat(h2.getTime()).isEqualTo("2026-01-01 10:30");
    }
}
