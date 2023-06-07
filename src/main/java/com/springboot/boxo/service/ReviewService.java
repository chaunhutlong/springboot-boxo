package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.ReviewDTO;
import com.springboot.boxo.payload.request.ReviewRequest;

import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(Long userId, ReviewRequest reviewRequest);
    List<ReviewDTO> getReviewByBookId(Long bookId);
    List<ReviewDTO> getReviewByUserId(Long userId);
}
