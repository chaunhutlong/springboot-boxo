package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Book;
import com.springboot.boxo.entity.Genre;
import com.springboot.boxo.entity.Review;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.dto.PythonBookDTO;
import com.springboot.boxo.payload.dto.PythonReviewDTO;
import com.springboot.boxo.payload.dto.RecommendationDTO;
import com.springboot.boxo.repository.BookRepository;
import com.springboot.boxo.repository.RecommendationService;
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
    private final ModelMapper modelMapper;

    public RecommendationServiceImpl(PythonServerService pythonServerService, BookRepository bookRepository, ModelMapper modelMapper) {
        this.pythonServerService = pythonServerService;
        this.bookRepository = bookRepository;
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

    public PaginationResponse<BookDTO> getRecommendationsByBookId(Long bookId, int pageNumber, int pageSize) {
        List<RecommendationDTO> bookDTOList = pythonServerService.getRecommendationsByBookId(bookId, pageNumber, pageSize);
        List<BookDTO> sortedBooks = getSortedBooks(bookDTOList);

        PaginationResponse<BookDTO> response = new PaginationResponse<>();
        response.setDatas(sortedBooks);
        response.setLimit(pageSize);
        response.setPage(pageNumber);
        response.setTotalResults(100);
        response.setTotalPages(100 / pageSize + 1);
        response.setLast(pageNumber == 100 / pageSize + 1);

        return response;
    }

    @Override
    public List<BookDTO> getRecommendationsHomePage() {
        List<RecommendationDTO> bookDTOList = pythonServerService.getRecommendationsHomePage();
        return getSortedBooks(bookDTOList);
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

    private BookDTO mapToBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}