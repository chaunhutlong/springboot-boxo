package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    // pagination
    Page<Genre> findAll(Pageable pageable);
}
