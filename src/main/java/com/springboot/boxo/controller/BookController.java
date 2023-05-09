package com.springboot.boxo.controller;

import com.springboot.boxo.payload.BookCreator;
import com.springboot.boxo.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("${spring.data.rest.base-path}/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpStatus> createBook(@Valid @ModelAttribute BookCreator bookRequest) {
        HttpStatus statusCode = bookService.createBook(bookRequest);
        return new ResponseEntity<>(statusCode);
    }

}
