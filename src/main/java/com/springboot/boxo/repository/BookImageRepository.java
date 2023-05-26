package com.springboot.boxo.repository;

import com.springboot.boxo.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM BookImage bi WHERE bi.book.id = :bookId")
    void deleteByBookId(Long bookId);
}