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
            "WHERE (similarity(unaccent(b.name), unaccent(:searchTerm)) > 0.3 " +
            "OR similarity(unaccent(b.description), unaccent(:searchTerm)) > 0.3 " +
            "OR unaccent(b.isbn) ILIKE '%' || unaccent(:searchTerm) || '%' ESCAPE '~') " +
            "AND (:genreId IS NULL OR EXISTS (SELECT 1 FROM b.genres g WHERE g.id = :genreId))")
    Page<Book> searchBooks(String searchTerm, Long genreId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
    Optional<Book> findByIsbn(String isbn);


    // Select the Book object with the embeddingId of bookImage table is null
    @Query("SELECT DISTINCT b FROM Book b JOIN b.images bi WHERE bi.embeddingId IS NULL")
    List<Book> findBooksWithNullEmbeddingId();

    // Find the books with limit
    @Query("SELECT b FROM Book b")
    List<Book> findWithLimit(int limit);
}
