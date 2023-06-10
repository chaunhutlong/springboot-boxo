package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g " +
            "WHERE similarity(unaccent(g.name), unaccent(:searchTerm)) > 0.5 " +
            "ORDER BY similarity(unaccent(g.name), unaccent(:searchTerm)) DESC")
    List<Genre> findTopBySearchTerm(String searchTerm);

    // Find genre with book title contain genre name
    // I pass the book name to this method
    // Apply case insensitive search
    @Query("SELECT g FROM Genre g " +
            "WHERE :bookName ILIKE CONCAT('%', g.name, '%')")
    List<Genre> findByBookName(String bookName);

    @Query("SELECT g FROM Genre g WHERE g.name = :name")
    Genre findByName(String name);

    @Query("SELECT g.name FROM Genre g")
    List<String> findAllGenreNames();
}
