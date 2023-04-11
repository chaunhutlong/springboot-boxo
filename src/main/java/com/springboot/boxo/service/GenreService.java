package com.springboot.boxo.service;

import com.springboot.boxo.payload.GenreDto;
import com.springboot.boxo.payload.GenreRequest;
import com.springboot.boxo.payload.GenreResponse;

public interface GenreService {
    GenreDto createGenre(GenreRequest genreRequest);
    GenreDto getGenreById(Long id);
    GenreResponse getAllGenres(int pageNumber, int pageSize, String sortBy, String sortDir);
    GenreDto updateGenre(Long id, GenreRequest genreRequest);
    void deleteGenre(Long id);
}
