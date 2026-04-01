package org.example.back.service.impl;

import org.example.back.entity.Review;
import org.example.back.entity.User;
import org.example.back.mapper.UserMapper;
import org.example.back.repository.ReviewRepository;
import org.example.back.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String createReview(Review review) {

        reviewRepository.save(review);

        // 更新被评价用户信用分
        User user = userMapper.selectById(review.getToUserId());

        int newScore = user.getCreditScore() + review.getScore();
        user.setCreditScore(newScore);

        userMapper.update(user);
        return review.toString();
    }
}