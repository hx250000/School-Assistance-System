package org.example.back.service;

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
        Review review = new Review();
        review.setToUserId(10L);
        review.setScore(5);

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(review))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户 10 未找到");

        verify(reviewRepository).save(review);
    }

    @Test
    void createReview_shouldUpdateCreditScore_andSaveUser() {
        Review review = new Review();
        review.setToUserId(10L);
        review.setScore(-2);

        User u = new User();
        u.setId(10L);
        u.setCreditScore(100);
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        Review res = reviewService.createReview(review);

        verify(reviewRepository).save(review);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getCreditScore()).isEqualTo(98);
    }
}

