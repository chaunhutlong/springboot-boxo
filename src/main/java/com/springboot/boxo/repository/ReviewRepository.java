package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);

    List<Review> findByUserId(Long userId);

    @Query("SELECT r.content, r.rating, r.book.id, r.user.id FROM Review r")
    List<Review> getReviewsForEmbedding();
}
