package org.example.back.service.impl;

import org.example.back.entity.Review;
import org.example.back.entity.User;
import org.example.back.mapper.ReviewMapper;
import org.example.back.mapper.UserMapper;
import org.example.back.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void createReview(Review review) {

        reviewMapper.insert(review);

        // 更新被评价用户信用分
        User user = userMapper.selectById(review.getToUserId());

        int newScore = user.getCreditScore() + review.getScore();
        user.setCreditScore(newScore);

        userMapper.update(user);
    }
}