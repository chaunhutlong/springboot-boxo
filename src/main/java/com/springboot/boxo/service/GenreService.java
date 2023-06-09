package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.GenreDTO;
import com.springboot.boxo.payload.request.GenreRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface GenreService {
    GenreDTO createGenre(GenreRequest genreRequest);
    GenreDTO getGenreById(Long id);
    PaginationResponse<GenreDTO> getAllGenres(int pageNumber, int pageSize, String sortBy, String sortDir);
    GenreDTO updateGenre(Long id, GenreRequest genreRequest);
    void deleteGenre(Long id);
}
