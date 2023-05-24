package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Publisher;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.PublisherDTO;
import com.springboot.boxo.payload.request.PublisherRequest;
import com.springboot.boxo.repository.PublisherRepository;
import com.springboot.boxo.service.PublisherService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {
    private static final String GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Publisher with id {0} not found";
    private final PublisherRepository publisherRepository;
    private final ModelMapper modelMapper;

    public PublisherServiceImpl(PublisherRepository publisherRepository, ModelMapper modelMapper) {
        this.publisherRepository = publisherRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PublisherDTO createPublisher(PublisherRequest publisherRequest) {
        Publisher publisher = mapToEntity(publisherRequest);
        Publisher newPublisher = publisherRepository.save(publisher);
        return mapToDTO(newPublisher);
    }

    @Override
    public PublisherDTO getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        return mapToDTO(publisher);
    }

    @Override
    public PaginationResponse<PublisherDTO> getAllPublishers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);

        Page<Publisher> publishers = publisherRepository.findAll(pageable);

        List<Publisher> listOfPublishers = publishers.getContent();
        List<PublisherDTO> content = listOfPublishers.stream().map(this::mapToDTO).toList();

        return PaginationUtils.createPaginationResponse(content, publishers);
    }

    @Override
    public PublisherDTO updatePublisher(Long id, PublisherRequest publisherRequest) {
        Publisher publisher = publisherRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        publisher.setName(publisherRequest.getName());
        Publisher updatedPublisher = publisherRepository.save(publisher);
        return mapToDTO(updatedPublisher);
    }

    @Override
    public void deletePublisher(Long id) {
        Publisher publisher = publisherRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        publisherRepository.delete(publisher);
    }

    private PublisherDTO mapToDTO(Publisher publisher) {
        return modelMapper.map(publisher, PublisherDTO.class);
    }

    private Publisher mapToEntity(PublisherRequest publisherRequest) {
        return modelMapper.map(publisherRequest, Publisher.class);
    }
}