package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.GenreDTO;
import com.springboot.boxo.payload.request.GenreRequest;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.service.GenreService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("${spring.data.rest.base-path}/genres")

public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GenreDTO> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<GenreDTO>> getAllGenres(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(genreService.getAllGenres(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable(value = "id") Long id,
                                                @Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.updateGenre(id, genreRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable(value = "id") Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
