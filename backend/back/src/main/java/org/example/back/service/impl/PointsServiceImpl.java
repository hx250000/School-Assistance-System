package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
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

import java.util.stream.Collectors;
import java.util.List;

@Service
public class PointsServiceImpl implements PointsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    @Override
    public Integer getUserPoints() {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("用户 %d 未找到", userId)
                ));
        return user.getPoints();
    }

    @Override
    @Transactional
    public void addPoints(Long userId, Integer points,String title,String desc) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("用户 %d 未找到", userId)
                ));

        user.setPoints(user.getPoints() + points);

        userRepository.save(user); 

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(points);
        log.setTitle(title);
        log.setDescription(desc);

        pointsLogRepository.save(log);
    }

    //添加积分记录查询功能
    @Override
    public List<UserPointsHistory> getMyPointsHistory(){
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        return pointsLogRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(log -> new UserPointsHistory(
                        log.getId(),
                        log.getChangeAmount(),
                        log.getTitle(),
                        log.getDescription(),
                        log.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}