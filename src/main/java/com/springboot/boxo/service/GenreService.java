package com.springboot.boxo.service;

import com.springboot.boxo.payload.GenreDto;
import com.springboot.boxo.payload.GenreRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface GenreService {
    GenreDto createGenre(GenreRequest genreRequest);
    GenreDto getGenreById(Long id);
    PaginationResponse<GenreDto> getAllGenres(int pageNumber, int pageSize, String sortBy, String sortDir);
    GenreDto updateGenre(Long id, GenreRequest genreRequest);
    void deleteGenre(Long id);
}
