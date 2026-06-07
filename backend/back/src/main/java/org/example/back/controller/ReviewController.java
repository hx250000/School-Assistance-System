package org.example.back.controller;

import org.example.back.common.ApiResponse;
import org.example.back.dto.request.ReviewCreateRequest;
import org.example.back.entity.Review;
import org.example.back.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create")
    public ApiResponse<Review> create(@RequestBody ReviewCreateRequest review) {

        return ApiResponse.success(reviewService.createReview(review));
    }
}