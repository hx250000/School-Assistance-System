package org.example.back.service.impl;

import org.example.back.entity.PointsLog;
import org.example.back.entity.User;
import org.example.back.mapper.PointsLogMapper;
import org.example.back.mapper.UserMapper;
import org.example.back.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointsLogMapper pointsLogMapper;

    @Override
    public Integer getUserPoints() {

        Long userId = 1L;

        User user = userMapper.selectById(userId);
        return user.getPoints();
    }

    @Override
    public void addPoints(Long userId, Integer points) {

        User user = userMapper.selectById(userId);

        user.setPoints(user.getPoints() + points);

        userMapper.update(user); // ← 用新增方法

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(points);
        log.setReason("任务奖励");

        pointsLogMapper.insert(log);
    }
}