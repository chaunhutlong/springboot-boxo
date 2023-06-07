package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.ReviewDTO;
import com.springboot.boxo.payload.request.ReviewRequest;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReviewRequest reviewRequest) {
        Long userId = userDetails.getUserId();
        var review = reviewService.createReview(userId, reviewRequest);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<List<ReviewDTO>> getReviewByBookId(@PathVariable(value = "id") Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewByBookId(bookId));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<ReviewDTO>> getReviewByUserId(@PathVariable(value = "id") Long userId) {
        return ResponseEntity.ok(reviewService.getReviewByUserId(userId));
    }

}
