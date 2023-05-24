package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.PublisherDTO;
import com.springboot.boxo.payload.request.PublisherRequest;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.service.PublisherService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("${spring.data.rest.base-path}/publishers")

public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PublisherDTO> createPublisher(@Valid @RequestBody PublisherRequest publisherRequest) {
        return ResponseEntity.ok(publisherService.createPublisher(publisherRequest));
    }

    @GetMapping
    public ResponseEntity<PaginationResponse<PublisherDTO>> getAllPublishers(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return ResponseEntity.ok(publisherService.getAllPublishers(pageNumber, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO> getPublisherById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PublisherDTO> updatePublisher(@PathVariable(value = "id") Long id,
                                                        @Valid @RequestBody PublisherRequest publisherRequest) {
        return ResponseEntity.ok(publisherService.updatePublisher(id, publisherRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable(value = "id") Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
