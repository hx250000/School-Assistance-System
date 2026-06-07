package org.example.back.service.impl;

import org.example.back.dto.request.ReviewCreateRequest;
import org.example.back.entity.Review;
import org.example.back.entity.User;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.ReviewRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.AchievementService;
import org.example.back.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementService achievementService;

    @Override
    public Review createReview(ReviewCreateRequest review) {

        // 先检查被评价用户是否存在
        User user = userRepository.findById(review.getToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        format("用户 %d 未找到", review.getToUserId())
                        ));

        // 更新被评价用户信用分
        int newScore = user.getCreditScore() + review.getScore();
        user.setCreditScore(newScore);
        userRepository.save(user);

        // 创建评价记录
        Review reviewEntity = new Review();
        reviewEntity.setContent(review.getContent());
        reviewEntity.setScore(review.getScore());
        reviewEntity.setFromUserId(review.getFromUserId());
        reviewEntity.setToUserId(review.getToUserId());
        reviewEntity.setTaskId(review.getTaskId());
        reviewRepository.save(reviewEntity);

        // 重新计算被评价用户的成就进度
        achievementService.recalculateUserAchievements(review.getToUserId());

        return reviewEntity;
    }
}