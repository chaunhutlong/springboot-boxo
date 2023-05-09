package com.springboot.boxo.service;

import com.springboot.boxo.payload.AuthorDTO;
import com.springboot.boxo.payload.AuthorRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface AuthorService {
    AuthorDTO createAuthor(AuthorRequest authorRequest);
    AuthorDTO getAuthorById(Long id);
    PaginationResponse<AuthorDTO> getAllAuthors(int pageNumber, int pageSize, String sortBy, String sortDir);
    AuthorDTO updateAuthor(Long id, AuthorRequest authorRequest);
    void deleteAuthor(Long id);
}
