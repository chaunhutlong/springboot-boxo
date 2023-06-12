package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Book;
import com.springboot.boxo.entity.Genre;
import com.springboot.boxo.entity.Review;
import com.springboot.boxo.payload.dto.*;
import com.springboot.boxo.payload.request.SearchImageRequest;
import com.springboot.boxo.repository.BookRepository;
import com.springboot.boxo.repository.RecommendationService;
import com.springboot.boxo.repository.ReviewRepository;
import com.springboot.boxo.service.PythonServerService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final PythonServerService pythonServerService;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    public RecommendationServiceImpl(PythonServerService pythonServerService, BookRepository bookRepository, ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.pythonServerService = pythonServerService;
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public void embeddingBooks() {
        List<Book> books = bookRepository.findAll();

        List<PythonBookDTO> pythonBookDTOList = books.stream().map(this::mapToPythonBookDTO).toList();

        List<Review> reviews = books.stream().flatMap(book -> book.getReviews().stream()).toList();

        List<PythonReviewDTO> pythonReviewDTOList = reviews.stream().map(this::mapToPythonReviewDTO).toList();

        pythonServerService.embeddingBooks(pythonBookDTOList, pythonReviewDTOList);
    }

    private List<BookDTO> getSortedBooks(List<RecommendationDTO> recommendationDTOList) {
        List<Long> bookIds = recommendationDTOList.stream()
                .map(RecommendationDTO::getBook_id)
                .toList();

        List<Book> books = bookRepository.findAllById(bookIds);

        // Sort the books based on the order of bookIds
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));

        List<Book> sortedBooks = bookIds.stream()
                .map(bookMap::get)
                .toList();

        return sortedBooks.stream().map(this::mapToBookDTO).toList();
    }

    private List<BookDTO> getBooks(List<ImageSimilarityDTO> imageSimilarityDTOList) {
        List<Long> bookIds = imageSimilarityDTOList.stream()
                .map(ImageSimilarityDTO::getBook_id)
                .toList();

        List<Book> books = bookRepository.findAllById(bookIds);

        // Sort the books based on the order of bookIds
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, book -> book));

        List<Book> sortedBooks = bookIds.stream()
                .map(bookMap::get)
                .toList();

        return sortedBooks.stream().map(this::mapToBookDTO).toList();
    }

    public List<BookDTO> getRecommendationsByBookId(Long bookId) {
        List<RecommendationDTO> bookDTOList = pythonServerService.getRecommendationsByBookId(bookId);

        return getSortedBooks(bookDTOList);
    }

    @Override
    public List<BookDTO> getRecommendationsHomePage() {
        List<RecommendationDTO> bookDTOList = pythonServerService.getRecommendationsHomePage();
        return getSortedBooks(bookDTOList);
    }

    @Override
    public List<BookDTO> getRecommendationsByImage(SearchImageRequest request) {
        List<ImageSimilarityDTO> bookDTOList = pythonServerService.getRecommendationsByImage(request.getImage());
        return getBooks(bookDTOList);
    }

    private PythonBookDTO mapToPythonBookDTO(Book book) {
        PythonBookDTO pythonBookDTO = new PythonBookDTO();
        pythonBookDTO.setBook_id(book.getId());
        pythonBookDTO.setTitle(book.getName());
        pythonBookDTO.setGenre_ids(book.getGenres().stream().map(Genre::getId).toList());

        return pythonBookDTO;
    }

    private PythonReviewDTO mapToPythonReviewDTO(Review review) {
        PythonReviewDTO pythonReviewDTO = new PythonReviewDTO();
        pythonReviewDTO.setBook_id(review.getBook().getId());
        pythonReviewDTO.setUser_id(review.getUser().getId());
        pythonReviewDTO.setRating(review.getRating());

        return pythonReviewDTO;
    }

    private BookDTO mapBookRating(BookDTO bookDTO) {
        List<Review> reviews = reviewRepository.findByBookId(bookDTO.getId());

        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            bookDTO.setRating(averageRating);
        }

        bookDTO.setRatingCount(reviews.size());

        return bookDTO;
    }

    private BookDTO mapToBookDTO(Book book) {
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        return mapBookRating(bookDTO);
    }
}