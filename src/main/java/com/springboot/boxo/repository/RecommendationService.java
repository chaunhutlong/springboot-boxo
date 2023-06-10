package com.springboot.boxo.repository;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;

import java.util.List;

public interface RecommendationService {
    void embeddingBooks();
    PaginationResponse<BookDTO> getRecommendationsByBookId(Long bookId, int pageNumber, int pageSize);
    List<BookDTO> getRecommendationsHomePage();
}
