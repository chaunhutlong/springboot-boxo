package com.springboot.boxo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.boxo.entity.BookImage;
import com.springboot.boxo.payload.dto.*;
import com.springboot.boxo.payload.request.PythonEmbeddingBooksRequest;
import com.springboot.boxo.repository.BookImageRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
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
            List<String> embeddingIds = objectMapper.readValue(responseBody, new TypeReference<>() {
            });

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

    public void embeddingBooks(List<PythonBookDTO> books, List<PythonReviewDTO> reviews) {
        String url = "http://localhost:5000/book-features";
        PythonEmbeddingBooksRequest request = new PythonEmbeddingBooksRequest();
        request.setBooks(books);
        request.setReviews(reviews);
        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the request entity with the headers and body
        HttpEntity<PythonEmbeddingBooksRequest> requestEntity = new HttpEntity<>(request, headers);

        // Send the POST request to the Python server
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
    }

    public List<RecommendationDTO> getRecommendationsByBookId(Long bookId) {
        // Prepare the request URL
        // default limit = 10, default page = 1
        String url = "http://localhost:5000/recommendations/books/" + bookId;

        // Send the GET request to the Python server
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String responseBody = responseEntity.getBody();

        // Deserialize the JSON response using ObjectMapper
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            // Handle the exception appropriately
        }
        return Collections.emptyList();
    }

    public List<RecommendationDTO> getRecommendationsHomePage() {
        // Prepare the request URL
        // default limit = 10, default page = 1
        String url = "http://localhost:5000/recommendations";

        // Send the GET request to the Python server
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String responseBody = responseEntity.getBody();

        // Deserialize the JSON response using ObjectMapper
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            // Handle the exception appropriately
        }
        return Collections.emptyList();
    }

    public List<ImageSimilarityDTO> getRecommendationsByImage(MultipartFile image) {
        String url = "http://localhost:5000/visual-search";

        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Build the request entity with the headers and body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", image.getResource());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send the POST request to the Python server
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();

        // Deserialize the JSON response using ObjectMapper
        try {
            return objectMapper.readValue(responseBody, new TypeReference<>() {
            });
        } catch (Exception e) {
            // Handle the exception appropriately
        }
        return Collections.emptyList();
    }

    private BookImageData convertToBookImageData(BookImage bookImage) {
        // Convert BookImage to BookImageData
        BookImageData bookImageData = new BookImageData();
        bookImageData.setId(bookImage.getId());
        bookImageData.setBookId(bookImage.getBook().getId());
        bookImageData.setEmbeddingId(bookImage.getEmbeddingId());
        bookImageData.setUrl(bookImage.getUrl());
        return bookImageData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookImageData {
        private Long id;
        private Long bookId;
        private String embeddingId;
        private String url;
    }

    @Data
    public static class PythonServerResponse {
        private List<String> embeddingIds;
    }
}
