package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.response.PointsHistoryResponse;
import org.example.back.dto.response.UserPointsHistory;
import org.example.back.entity.PointsLog;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.PointsLogRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    // ================= 查询积分 =================
    @Override
    public Integer getUserPoints() {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        return user.getPoints();
    }

    // ================= 安全加分 =================
    @Override
    @Transactional
    public void addPoints(Long userId,
                          Integer points,
                          String title,
                          String desc) {

        if (userId == null || points == null || points == 0) {
            throw new IllegalArgumentException("非法积分操作");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        // ❗ 防止异常负分过大
        if (user.getPoints() + points < 0) {
            throw new IllegalArgumentException("积分不足");
        }

        // ================= 幂等关键点 =================
        // 👉 同一个 title + userId + changeAmount 只能记录一次
        boolean exists = pointsLogRepository
                .existsByUserIdAndTitleAndChangeAmount(userId, title, points);

        if (exists) {
            return; // ❗ 防刷积分
        }

        user.setPoints(user.getPoints() + points);
        userRepository.save(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(points);
        log.setTitle(title);
        log.setDescription(desc);

        pointsLogRepository.save(log);
    }

    // ================= 积分历史 =================
    @Override
    public PointsHistoryResponse getMyPointsHistory() {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        return getUserPointsHistory(userId);
    }

    // 管理用
    @Override
    public PointsHistoryResponse getSomeonesPointsHistory(long userId) {
        return getUserPointsHistory(userId);
    }

    public PointsHistoryResponse getUserPointsHistory(long userId){
        List<PointsLog> logs =
                pointsLogRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<UserPointsHistory> historyList = logs.stream()
                .map(log -> new UserPointsHistory(
                        log.getChangeAmount(),
                        log.getTitle(),
                        log.getDescription(),
                        log.getCreatedAt()
                ))
                .toList();

        int increase = logs.stream()
                .filter(l -> l.getChangeAmount() > 0)
                .mapToInt(PointsLog::getChangeAmount)
                .sum();

        int decrease = logs.stream()
                .filter(l -> l.getChangeAmount() < 0)
                .mapToInt(PointsLog::getChangeAmount)
                .sum();

        PointsHistoryResponse response = new PointsHistoryResponse();
        response.setPointsHistoryList(historyList);
        response.setIncreasePoints(increase);
        response.setDecreasePoints(decrease);

        return response;
    }
}