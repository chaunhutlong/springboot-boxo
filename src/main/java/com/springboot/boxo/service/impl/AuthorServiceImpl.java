package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Author;
import com.springboot.boxo.payload.AuthorDto;
import com.springboot.boxo.payload.AuthorRequest;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.repository.AuthorRepository;
import com.springboot.boxo.service.AuthorService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final ModelMapper mapper;
    private static final String AUTHOR_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Author with id {0} not found";


    public AuthorServiceImpl(AuthorRepository authorRepository, ModelMapper mapper) {
        this.authorRepository = authorRepository;
        this.mapper = mapper;
    }

    @Override
    public AuthorDto createAuthor(AuthorRequest authorRequest) {
        Author author = mapToEntity(authorRequest);
        Author newAuthor = authorRepository.save(author);
        return mapToDTO(newAuthor);
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(AUTHOR_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        return mapToDTO(author);
    }

    @Override
    public PaginationResponse<AuthorDto> getAllAuthors(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<Author> authors = authorRepository.findAll(pageable);

        List<Author> listOfAuthors = authors.getContent();
        List<AuthorDto> content = listOfAuthors.stream().map(this::mapToDTO).toList();

        return PaginationUtils.createPaginationResponse(content, authors);
    }

    @Override
    public AuthorDto updateAuthor(Long id, AuthorRequest authorRequest) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(AUTHOR_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        author.setName(authorRequest.getName());
        Author updatedAuthor = authorRepository.save(author);
        return mapToDTO(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(AUTHOR_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        authorRepository.delete(author);
    }

    private AuthorDto mapToDTO(Author author) {
        return mapper.map(author, AuthorDto.class);
    }

    private Author mapToEntity(AuthorRequest authorRequest) {
        return mapper.map(authorRequest, Author.class);
    }
}