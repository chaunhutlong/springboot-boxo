package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookRequest;
import org.springframework.http.HttpStatus;

public interface BookService {
    HttpStatus createBook(BookRequest bookRequest);
    HttpStatus updateBook(Long id, BookRequest bookRequest);
    BookDTO getBookById(Long id);
}
