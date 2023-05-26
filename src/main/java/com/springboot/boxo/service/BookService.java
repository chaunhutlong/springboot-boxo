package com.springboot.boxo.service;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookRequest;
import org.springframework.http.HttpStatus;

public interface BookService {
    PaginationResponse<BookDTO> getAllBooks(int pageNumber, int pageSize, String sortBy, String sortDir);
    HttpStatus createBook(BookRequest bookRequest);
    HttpStatus updateBook(Long id, BookRequest bookRequest);
    BookDTO getBookById(Long id);
    HttpStatus deleteBookById(Long id);
}
