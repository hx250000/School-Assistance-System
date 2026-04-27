package org.example.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.back.entity.Review;
import org.example.back.exception.GlobalExceptionHandler;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.service.ReviewService;
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
@Import(GlobalExceptionHandler.class)
class ReviewControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    void create_shouldReturnString() throws Exception {
        when(reviewService.createReview(any(Review.class))).thenReturn("ok");

        Review r = new Review();
        r.setToUserId(1L);
        r.setScore(1);

        mockMvc.perform(post("/api/review/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void create_whenServiceThrowsNotFound_shouldMapTo404() throws Exception {
        when(reviewService.createReview(any(Review.class))).thenThrow(new ResourceNotFoundException("用户不存在"));

        Review r = new Review();
        r.setToUserId(999L);
        r.setScore(1);

        mockMvc.perform(post("/api/review/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}

