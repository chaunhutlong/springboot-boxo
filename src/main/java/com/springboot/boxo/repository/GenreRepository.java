package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Genre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g " +
            "WHERE similarity(unaccent(g.name), unaccent(:searchTerm)) > 0.6 " +
            "ORDER BY similarity(unaccent(g.name), unaccent(:searchTerm)) DESC")
    List<Genre> findTopBySearchTerm(String searchTerm, Pageable pageable);

    @Query("SELECT g FROM Genre g WHERE LOWER(:bookTitle) LIKE LOWER(CONCAT('%', g.name, '%'))")
    List<Genre> findByBookTitleContainingGenres(String bookTitle);
}
