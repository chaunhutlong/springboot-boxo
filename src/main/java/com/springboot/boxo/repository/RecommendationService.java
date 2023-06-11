package com.springboot.boxo.repository;

import com.springboot.boxo.payload.dto.BookDTO;

import java.util.List;

public interface RecommendationService {
    void embeddingBooks();
    List<BookDTO> getRecommendationsByBookId(Long bookId);
    List<BookDTO> getRecommendationsHomePage();
}
