package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g " +
            "WHERE similarity(unaccent(g.name), unaccent(:searchTerm)) > 0.5 " +
            "ORDER BY similarity(unaccent(g.name), unaccent(:searchTerm)) DESC")
    List<Genre> findTopBySearchTerm(String searchTerm);

    @Query("SELECT g FROM Genre g WHERE g.name = :name")
    Genre findByName(String name);

    // my genre something is Business/Entrepreneurship
    // So i want if book title contains Business or Entrepreneurship, it will show the genre
    @Query("SELECT g FROM Genre g WHERE g.name IN :genreNames " +
            "AND (:bookTitle LIKE CONCAT('%', g.name, '%'))")
    List<Genre> findByBookTitleContainingGenres(@Param("bookTitle") String bookTitle, @Param("genreNames") List<String> genreNames);

    @Query("SELECT g.name FROM Genre g")
    List<String> findAllGenreNames();
}
