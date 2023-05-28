package com.springboot.boxo.repository;

import com.springboot.boxo.entity.Book;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @NotNull Page<Book> findAll(@NotNull Pageable pageable);
    @Query("SELECT b FROM Book b " +
            "WHERE similarity(unaccent(b.name), unaccent(:searchTerm)) > 0.3 " +
            "OR similarity(unaccent(b.description), unaccent(:searchTerm)) > 0.3 " +
            "OR unaccent(b.isbn) ILIKE '%' || unaccent(:searchTerm) || '%' ESCAPE '~' " +
            "ORDER BY similarity(unaccent(b.name), unaccent(:searchTerm)) DESC, " +
            "similarity(unaccent(b.description), unaccent(:searchTerm)) DESC")
    List<Book> searchBooks(String searchTerm);

    @Query("SELECT b FROM Book b " +
            "WHERE similarity(unaccent(b.name), unaccent(:searchTerm)) > 0.3 " +
            "OR similarity(unaccent(b.description), unaccent(:searchTerm)) > 0.3 " +
            "OR unaccent(b.isbn) ILIKE '%' || unaccent(:searchTerm) || '%' ESCAPE '~'")
    Page<Book> searchBooks(String searchTerm, Pageable pageable);

    Optional<Book> findByIsbn(String isbn);
}
