package com.springboot.boxo.repository;

import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.SearchImageRequest;

import java.util.List;

public interface RecommendationService {
    void embeddingBooks();
    List<BookDTO> getRecommendationsByBookId(Long bookId);
    List<BookDTO> getRecommendationsHomePage();
    List<BookDTO> getRecommendationsByImage(SearchImageRequest image);
}
