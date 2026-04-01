package org.example.back.service.impl;

import org.example.back.entity.PointsLog;
import org.example.back.entity.User;
import org.example.back.repository.PointsLogRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    @Override
    public Integer getUserPoints() {

        Long userId = 1L;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("User %d not found", userId)
                ));
        return user.getPoints();
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("User %d not found", userId)
                ));

        user.setPoints(user.getPoints() + points);

        userRepository.save(user); // ← 用新增方法

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(points);
        log.setReason("任务奖励");

        pointsLogRepository.save(log);
    }
}