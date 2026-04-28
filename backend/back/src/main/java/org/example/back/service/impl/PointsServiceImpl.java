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
    public PointsHistoryResponse getMyPointsHistory(){
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }

        //获取所有记录
        List<PointsLog> logs = pointsLogRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 2. 转换为 DTO 列表
        List<UserPointsHistory> historyList = logs.stream()
                .map(log -> new UserPointsHistory(
                        log.getChangeAmount(),
                        log.getTitle(),
                        log.getDescription(),
                        log.getCreatedAt()
                ))
                .toList();

        // 3. 计算累计增加和减少
        int increase = logs.stream()
                .filter(log -> log.getChangeAmount() > 0)
                .mapToInt(PointsLog::getChangeAmount)
                .sum();

        int decrease = logs.stream()
                .filter(log -> log.getChangeAmount() < 0)
                .mapToInt(PointsLog::getChangeAmount)
                .sum();

        // 4. 封装返回
        PointsHistoryResponse response = new PointsHistoryResponse();
        response.setPointsHistoryList(historyList);
        response.setIncreasePoints(increase);
        response.setDecreasePoints(decrease);

        System.out.println("response: " + response);

        return response;

//        return pointsLogRepository.findByUserIdOrderByCreatedAtDesc(userId)
//                .stream()
//                .map(log -> new UserPointsHistory(
//                        log.getChangeAmount(),
//                        log.getTitle(),
//                        log.getDescription(),
//                        log.getCreatedAt()
//                ))
//                .collect(Collectors.toList());
    }
}