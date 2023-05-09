package com.springboot.boxo.service;

import com.springboot.boxo.payload.BookCreator;
import org.springframework.http.HttpStatus;

public interface BookService {
    HttpStatus createBook(BookCreator bookRequest);
}
