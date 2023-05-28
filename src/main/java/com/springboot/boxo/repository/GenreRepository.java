package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g " +
            "WHERE similarity(unaccent(g.name), unaccent(:searchTerm)) > 0.3 " +
            "ORDER BY similarity(unaccent(g.name), unaccent(:searchTerm)) DESC")
    List<Genre> findBySearchTerm(String searchTerm);

    Genre findByName(String name);
}
