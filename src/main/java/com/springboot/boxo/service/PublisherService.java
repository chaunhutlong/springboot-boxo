package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.PublisherDTO;
import com.springboot.boxo.payload.request.PublisherRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface PublisherService {
    PublisherDTO createPublisher(PublisherRequest genreRequest);
    PublisherDTO getPublisherById(Long id);
    PaginationResponse<PublisherDTO> getAllPublishers(int pageNumber, int pageSize, String sortBy, String sortDir);
    PublisherDTO updatePublisher(Long id, PublisherRequest genreRequest);
    void deletePublisher(Long id);
}
