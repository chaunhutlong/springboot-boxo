package com.springboot.boxo.controller;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookRequest;
import com.springboot.boxo.payload.request.BookCrawlRequest;
import com.springboot.boxo.service.BookService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("${spring.data.rest.base-path}/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<BookDTO>> getAllBooks(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(value = "search", defaultValue = "", required = false) String searchTerm

    ) {
        return ResponseEntity.ok(bookService.getAllBooks(searchTerm, pageNumber, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpStatus> createBook(@Valid @ModelAttribute BookRequest bookRequest) {
        HttpStatus statusCode = bookService.createBook(bookRequest);
        return new ResponseEntity<>(statusCode);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpStatus> updateBook(
            @PathVariable(value = "id") Long id,
            @Valid @ModelAttribute BookRequest bookRequest) {
        HttpStatus statusCode = bookService.updateBook(id, bookRequest);
        return new ResponseEntity<>(statusCode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBookById(@PathVariable(value = "id") Long id) {
        HttpStatus statusCode = bookService.deleteBookById(id);
        return new ResponseEntity<>(statusCode);
    }

    @PostMapping("/crawl")
    public ResponseEntity<List<BookDTO>> crawlBooks(@RequestBody BookCrawlRequest crawlBooksRequest) {
        return ResponseEntity.ok(bookService.crawlBooks(crawlBooksRequest));
    }

    @PostMapping("/sync-book-images")
    public ResponseEntity<HttpStatus> syncBookImages() {
        HttpStatus statusCode = bookService.syncBookImages();
        return new ResponseEntity<>(statusCode);
    }
}
