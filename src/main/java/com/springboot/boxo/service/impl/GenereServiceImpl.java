package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Genre;
import com.springboot.boxo.payload.GenreDto;
import com.springboot.boxo.payload.GenreRequest;
import com.springboot.boxo.payload.GenreResponse;
import com.springboot.boxo.repository.GenreRepository;
import com.springboot.boxo.service.GenreService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenereServiceImpl implements GenreService {
    private GenreRepository genreRepository;
    private ModelMapper mapper;

    public GenereServiceImpl(GenreRepository genreRepository, ModelMapper mapper) {
        this.genreRepository = genreRepository;
        this.mapper = mapper;
    }

    @Override
    public GenreDto createGenre(GenreRequest genreRequest) {
        Genre genre = mapToEntity(genreRequest);
        Genre newGenre = genreRepository.save(genre);
        return mapToDTO(newGenre);
    }

                                @Override
    public GenreDto getGenreById(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
        GenreDto genreDto = mapToDTO(genre);
        return genreDto;
    }

    @Override
    public GenreResponse getAllGenres(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Genre> genres = genreRepository.findAll(pageable);

        // get content for page object
        List<Genre> listOfGenres = genres.getContent();

        List<GenreDto> content = listOfGenres.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());

        GenreResponse GenreResponse = new GenreResponse();
        GenreResponse.setContent(content);
        GenreResponse.setPageNumber(genres.getNumber());
        GenreResponse.setPageSize(genres.getSize());
        GenreResponse.setTotalElements(genres.getTotalElements());
        GenreResponse.setTotalPages(genres.getTotalPages());
        GenreResponse.setLast(genres.isLast());

        return GenreResponse;
    }

    @Override
    public GenreDto updateGenre(Long id, GenreRequest genreRequest) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
        genre.setName(genreRequest.getName());
        Genre updatedGenre = genreRepository.save(genre);
        return mapToDTO(updatedGenre);
    }

    @Override
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
        genreRepository.delete(genre);
    }

    private GenreDto mapToDTO(Genre genre) {
        GenreDto genreDto = mapper.map(genre, GenreDto.class);
        return genreDto;
    }

    private Genre mapToEntity(GenreRequest genreRequest) {
        Genre genre = mapper.map(genreRequest, Genre.class);
        return genre;
    }
}