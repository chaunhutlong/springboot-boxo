package com.springboot.boxo.service;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookRequest;
import com.springboot.boxo.payload.request.BookCrawlRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface BookService {
    PaginationResponse<BookDTO> getAllBooks(String searchTerm, int pageNumber, int pageSize, String sortBy, String sortDir);
    HttpStatus createBook(BookRequest bookRequest);
    HttpStatus updateBook(Long id, BookRequest bookRequest);
    BookDTO getBookById(Long id);
    HttpStatus deleteBookById(Long id);
    List<BookDTO> crawlBooks(BookCrawlRequest crawlBooksRequest);
    HttpStatus syncBookImages();
}
