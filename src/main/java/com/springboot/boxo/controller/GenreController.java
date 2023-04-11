package com.springboot.boxo.controller;

import com.springboot.boxo.payload.GenreDto;
import com.springboot.boxo.payload.GenreRequest;
import com.springboot.boxo.payload.GenreResponse;
import com.springboot.boxo.service.GenreService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("${spring.data.rest.base-path}/genres")

public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.createGenre(genreRequest));
    }

    @GetMapping
    public ResponseEntity<GenreResponse> getAllGenres(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(genreService.getAllGenres(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable(value = "id") Long id,
                                                @Valid @RequestBody GenreRequest genreRequest) {
        return ResponseEntity.ok(genreService.updateGenre(id, genreRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable(value = "id") Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
