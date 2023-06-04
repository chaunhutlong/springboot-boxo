package com.springboot.boxo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.boxo.entity.BookImage;
import com.springboot.boxo.repository.BookImageRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PythonServerService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final BookImageRepository bookImageRepository;

    public PythonServerService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
                               BookImageRepository bookImageRepository) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
        this.bookImageRepository = bookImageRepository;
    }

    public void sendBookImages(List<BookImage> bookImages) {
        // Prepare the request URL and body
        String url = "http://localhost:5000/book-images";
        List<BookImageData> bookImageDataList = bookImages.stream()
                .map(this::convertToBookImageData)
                .toList();

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the request entity with the headers and body
        HttpEntity<List<BookImageData>> requestEntity = new HttpEntity<>(bookImageDataList, headers);

        // Send the POST request to the Python server
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();

        // Deserialize the JSON response manually
        try {
            List<String> embeddingIds = objectMapper.readValue(responseBody, new TypeReference<>() {});

            // Update the corresponding BookImage objects with the feature_vector_ids
            for (int i = 0; i < bookImages.size(); i++) {
                BookImage bookImage = bookImages.get(i);
                String embeddingId = embeddingIds.get(i);
                bookImage.setEmbeddingId(embeddingId);
                bookImageRepository.save(bookImage);
            }
        } catch (Exception e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
    }

    private BookImageData convertToBookImageData(BookImage bookImage) {
        // Convert BookImage to BookImageData
        BookImageData bookImageData = new BookImageData();
        bookImageData.setId(bookImage.getId());
        bookImageData.setEmbeddingId(bookImage.getEmbeddingId());
        bookImageData.setUrl(bookImage.getUrl());
        return bookImageData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookImageData {
        private Long id;
        private String embeddingId;
        private String url;
    }

    @Data
    public static class PythonServerResponse {
        private List<String> embeddingIds;
    }
}
