package org.example.back.service;

import org.example.back.dto.request.ReviewCreateRequest;
import org.example.back.entity.Review;

public interface ReviewService {

    Review createReview(ReviewCreateRequest review);
}