package com.springboot.boxo.service;

import com.springboot.boxo.payload.AuthorDto;
import com.springboot.boxo.payload.AuthorRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface AuthorService {
    AuthorDto createAuthor(AuthorRequest authorRequest);
    AuthorDto getAuthorById(Long id);
    PaginationResponse<AuthorDto> getAllAuthors(int pageNumber, int pageSize, String sortBy, String sortDir);
    AuthorDto updateAuthor(Long id, AuthorRequest authorRequest);
    void deleteAuthor(Long id);
}
