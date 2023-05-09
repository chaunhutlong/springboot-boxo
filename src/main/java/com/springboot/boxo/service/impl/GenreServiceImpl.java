package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Genre;
import com.springboot.boxo.payload.GenreDTO;
import com.springboot.boxo.payload.GenreRequest;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.repository.GenreRepository;
import com.springboot.boxo.service.GenreService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    private static final String GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Genre with id {0} not found";
    private final GenreRepository genreRepository;
    private final ModelMapper modelMapper;

    public GenreServiceImpl(GenreRepository genreRepository, ModelMapper modelMapper) {
        this.genreRepository = genreRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public GenreDTO createGenre(GenreRequest genreRequest) {
        Genre genre = mapToEntity(genreRequest);
        Genre newGenre = genreRepository.save(genre);
        return mapToDTO(newGenre);
    }

    @Override
    public GenreDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        return mapToDTO(genre);
    }

    @Override
    public PaginationResponse<GenreDTO> getAllGenres(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<Genre> genres = genreRepository.findAll(pageable);

        List<Genre> listOfGenres = genres.getContent();
        List<GenreDTO> content = listOfGenres.stream().map(this::mapToDTO).toList();

        return PaginationUtils.createPaginationResponse(content, genres);
    }

    @Override
    public GenreDTO updateGenre(Long id, GenreRequest genreRequest) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        genre.setName(genreRequest.getName());
        Genre updatedGenre = genreRepository.save(genre);
        return mapToDTO(updatedGenre);
    }

    @Override
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        genreRepository.delete(genre);
    }

    private GenreDTO mapToDTO(Genre genre) {
        return modelMapper.map(genre, GenreDTO.class);
    }

    private Genre mapToEntity(GenreRequest genreRequest) {
        return modelMapper.map(genreRequest, Genre.class);
    }
}