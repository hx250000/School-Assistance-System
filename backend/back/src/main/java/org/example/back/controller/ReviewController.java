package org.example.back.controller;

import org.example.back.common.Result;
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
    public Result create(@RequestBody Review review) {
        reviewService.createReview(review);
        return Result.success("评价成功");
    }
}