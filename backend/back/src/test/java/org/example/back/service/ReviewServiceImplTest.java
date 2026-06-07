package org.example.back.service;

import org.example.back.dto.request.ReviewCreateRequest;
import org.example.back.entity.Review;
import org.example.back.entity.User;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.ReviewRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void createReview_whenToUserNotFound_shouldThrowResourceNotFoundException() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setToUserId(10L);
        request.setScore(5);

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户 10 未找到");

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_shouldUpdateCreditScore_andSaveUser() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setToUserId(10L);
        request.setScore(-2);
        request.setContent("测试评价");

        User u = new User();
        u.setId(10L);
        u.setCreditScore(100);
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Review savedReview = new Review();
        savedReview.setId(1L);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        Review res = reviewService.createReview(request);

        verify(reviewRepository).save(any(Review.class));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getCreditScore()).isEqualTo(98);
    }

    @Test
    void createReview_shouldSaveReviewWithCorrectFields() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setTaskId(1L);
        request.setFromUserId(5L);
        request.setToUserId(10L);
        request.setScore(3);
        request.setContent("Good job");

        User u = new User();
        u.setId(10L);
        u.setCreditScore(100);
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Review savedReview = new Review();
        savedReview.setId(1L);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        reviewService.createReview(request);

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewCaptor.capture());
        
        Review capturedReview = reviewCaptor.getValue();
        assertThat(capturedReview.getTaskId()).isEqualTo(1L);
        assertThat(capturedReview.getFromUserId()).isEqualTo(5L);
        assertThat(capturedReview.getToUserId()).isEqualTo(10L);
        assertThat(capturedReview.getScore()).isEqualTo(3);
        assertThat(capturedReview.getContent()).isEqualTo("Good job");
    }

    @Test
    void createReview_whenPositiveScore_shouldIncreaseCreditScore() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setToUserId(10L);
        request.setScore(3);

        User u = new User();
        u.setId(10L);
        u.setCreditScore(100);
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Review savedReview = new Review();
        savedReview.setId(1L);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        reviewService.createReview(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getCreditScore()).isEqualTo(103);
    }

    @Test
    void createReview_whenNegativeScore_shouldDecreaseCreditScore() {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setToUserId(10L);
        request.setScore(-2);

        User u = new User();
        u.setId(10L);
        u.setCreditScore(100);
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Review savedReview = new Review();
        savedReview.setId(1L);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        reviewService.createReview(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getCreditScore()).isEqualTo(98);
    }
}