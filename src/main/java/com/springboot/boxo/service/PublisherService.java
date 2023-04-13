package com.springboot.boxo.service;

import com.springboot.boxo.payload.PublisherDto;
import com.springboot.boxo.payload.PublisherRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface PublisherService {
    PublisherDto createPublisher(PublisherRequest genreRequest);
    PublisherDto getPublisherById(Long id);
    PaginationResponse<PublisherDto> getAllPublishers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PublisherDto updatePublisher(Long id, PublisherRequest genreRequest);
    void deletePublisher(Long id);
}
