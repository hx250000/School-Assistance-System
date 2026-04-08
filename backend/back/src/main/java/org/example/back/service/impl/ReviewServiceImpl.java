package org.example.back.service.impl;

import org.example.back.entity.Review;
import org.example.back.entity.User;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.ReviewRepository;
import org.example.back.repository.UserRepository;
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

    @Override
    public String createReview(Review review) {

        reviewRepository.save(review);

        // 更新被评价用户信用分
        //User user = userMapper.selectById(review.getToUserId());
        User user = userRepository.findById(review.getToUserId()).
                    orElseThrow(() -> new ResourceNotFoundException(
                        format("用户 %d 未找到", review.getToUserId())
                        ));

        int newScore = user.getCreditScore() + review.getScore();
        user.setCreditScore(newScore);

        userRepository.save(user);
        return review.toString();
    }
}