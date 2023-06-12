package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.SearchImageRequest;
import com.springboot.boxo.repository.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/embedding-books")
    public ResponseEntity<Void> embeddingBooks() {
        recommendationService.embeddingBooks();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Recommendation by book id
    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<BookDTO>> getRecommendationsByBookId(
            @PathVariable(value = "bookId") Long bookId)
    {
        return ResponseEntity.ok(recommendationService.getRecommendationsByBookId(bookId));
    }

    // Recommendation Home Page
    @GetMapping("/home")
    public ResponseEntity<List<BookDTO>> getRecommendationsHomePage()
    {
        return ResponseEntity.ok(recommendationService.getRecommendationsHomePage());
    }

    @PostMapping("/image")
    public ResponseEntity<List<BookDTO>> getRecommendationsByImage(
            @ModelAttribute SearchImageRequest image)
    {
        return ResponseEntity.ok(recommendationService.getRecommendationsByImage(image));
    }
}
