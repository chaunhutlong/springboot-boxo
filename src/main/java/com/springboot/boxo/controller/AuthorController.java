package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.AuthorDTO;
import com.springboot.boxo.payload.request.AuthorRequest;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.service.AuthorService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("${spring.data.rest.base-path}/authors")

public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.ok(authorService.createAuthor(authorRequest));
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<AuthorDTO>> getAllAuthors(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(authorService.getAllAuthors(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable(value = "id") Long id,
                                                  @Valid @RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable(value = "id") Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
