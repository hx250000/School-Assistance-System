package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.config.SecurityConfig;
import org.example.back.dto.request.ReviewCreateRequest;
import org.example.back.entity.Review;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.service.ReviewService;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ReviewControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private static final Long TEST_USER_ID = 1L;

    @Test
    void create_shouldReturnReview() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setTaskId(1L);
        request.setFromUserId(1L);
        request.setToUserId(2L);
        request.setScore(5);
        request.setContent("Great!");

        Review response = new Review();
        response.setId(1L);
        response.setScore(5);
        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/review/create")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.score").value(5));
    }

    @Test
    void create_whenServiceThrowsNotFound_shouldMapTo404() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setToUserId(999L);
        request.setScore(1);

        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenThrow(new ResourceNotFoundException("用户不存在"));

        mockMvc.perform(post("/api/review/create")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void create_withNegativeScore_shouldReturnSuccess() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setTaskId(1L);
        request.setFromUserId(1L);
        request.setToUserId(2L);
        request.setScore(-2);
        request.setContent("Not good");

        Review response = new Review();
        response.setId(1L);
        response.setScore(-2);
        when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/review/create")
                        .header("Authorization", AuthTestUtil.createAuthorizationHeader(TEST_USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.score").value(-2));
    }
}