package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Book;
import com.springboot.boxo.entity.Review;
import com.springboot.boxo.payload.dto.ReviewDTO;
import com.springboot.boxo.payload.request.ReviewRequest;
import com.springboot.boxo.repository.BookRepository;
import com.springboot.boxo.repository.ReviewRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ModelMapper modelMapper, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewDTO createReview(Long userId, ReviewRequest reviewRequest) {
        Review review = convertToEntity(reviewRequest);
        review.setUser(userRepository.findById(userId).orElseThrow());
        return convertToDTO(reviewRepository.save(review));
    }

    @Override
    public List<ReviewDTO> getReviewByBookId(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        return reviews.stream().map(this::convertToDTO).toList();
    }

    @Override
    public List<ReviewDTO> getReviewByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream().map(this::convertToDTO).toList();
    }

    private Review convertToEntity(ReviewRequest reviewRequest) {
        Book book = bookRepository.findById(reviewRequest.getBookId()).orElseThrow();
        Review review = modelMapper.map(reviewRequest, Review.class);
        review.setBook(book);
        return review;
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        reviewDTO.setBookId(review.getBook().getId());
        return reviewDTO;
    }
}
