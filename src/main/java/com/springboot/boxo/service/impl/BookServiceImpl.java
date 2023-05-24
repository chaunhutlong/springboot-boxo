package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.*;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookRequest;
import com.springboot.boxo.repository.AuthorRepository;
import com.springboot.boxo.repository.BookRepository;
import com.springboot.boxo.repository.GenreRepository;
import com.springboot.boxo.repository.PublisherRepository;
import com.springboot.boxo.service.BookService;
import com.springboot.boxo.service.StorageService;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    private final ModelMapper createBookModelMapper;
    private final ModelMapper updateBookModelMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, PublisherRepository publisherRepository, GenreRepository genreRepository, AuthorRepository authorRepository, ModelMapper modelMapper, StorageService storageService) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;

        this.createBookModelMapper = new ModelMapper();
        configureCreateBookModelMapper();

        this.updateBookModelMapper = new ModelMapper();
        configureUpdateBookModelMapper();
    }

    private void configureCreateBookModelMapper() {
        createBookModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        createBookModelMapper.addMappings(new PropertyMap<BookRequest, Book>() {
            @Override
            protected void configure() {
                skip(destination.getImages());
                skip(destination.getPublisher());
                skip(destination.getGenres());
                skip(destination.getAuthors());
            }
        });
    }

    private void configureUpdateBookModelMapper() {
        updateBookModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        updateBookModelMapper.addMappings(new PropertyMap<BookRequest, Book>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getImages());
                skip(destination.getPublisher());
                skip(destination.getGenres());
                skip(destination.getAuthors());
            }
        });
    }
    @Override
    public HttpStatus createBook(@ModelAttribute BookRequest bookRequest)  {
        try {
            Book book = mapBookRequestToCreateBook(bookRequest);
            uploadBookImages(book, bookRequest.getImages());

            bookRepository.save(book);

            return HttpStatus.CREATED;
        } catch (Exception e) {
            HttpStatus  statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
    }

    @Override
    public HttpStatus updateBook(Long id, @ModelAttribute BookRequest bookRequest) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

            mapBookRequestToUpdateBook(bookRequest, book);
            // if images start https then don't upload to s3
            if (bookRequest.getImages() != null && !bookRequest.getImages().isEmpty() && !bookRequest.getImages().get(0).startsWith("https")) {
                uploadBookImages(book, bookRequest.getImages());
            }

            bookRepository.save(book);
            return HttpStatus.OK;
        } catch (Exception e) {
            HttpStatus  statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        return mapToDTO(book);
    }

    private void uploadBookImages(Book book, List<String> images) {
        if (images != null && !images.isEmpty()) {
            if (images.size() == 2 && (!images.get(0).contains(","))) {
                String combinedImage = images.get(0) + "," + images.get(1);
                images = List.of(combinedImage);
            }
            List<BookImage> bookImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String imageKey = book.getIsbn() + "_" + i;
                storageService.uploadBase64ToS3(images.get(i), imageKey);
                BookImage bookImage = new BookImage();
                bookImage.setImageKey(imageKey);
                bookImage.setBook(book);
                bookImages.add(bookImage);
            }
            book.setImages(bookImages);
        }
    }

    private void mapBookRequestToUpdateBook(BookRequest bookRequest, Book book) {

        mapPublisher(book, bookRequest.getPublisherId());
        mapAuthors(book, bookRequest.getAuthorIds());
        mapGenres(book, bookRequest.getGenreIds());

        updateBookModelMapper.map(bookRequest, book);
    }

    private void mapPublisher(Book book, Long publisherId) {
        Publisher existingPublisher = book.getPublisher(); // Get the current publisher

        if (existingPublisher == null || !existingPublisher.getId().equals(publisherId)) {
            Publisher newPublisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", publisherId));

            book.setPublisher(newPublisher);
        }
    }

    private void mapAuthors(Book book, Object authorIds) {
        if (authorIds instanceof Iterable<?>) {
            Iterable<Long> ids = castToIterableOfLong(authorIds);
            Set<Author> existingAuthors = book.getAuthors(); // Get the current authors

            // Initialize existingAuthors as an empty set if it is null
            if (existingAuthors == null) {
                existingAuthors = new HashSet<>();
            }

            // Create a final copy of the existingAuthors set for reference in lambda expression
            final Set<Author> finalExistingAuthors = existingAuthors;

            // Fetch the authors from the database that match the given IDs
            Set<Author> authors = StreamSupport.stream(ids.spliterator(), false)
                    .map(authorRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            // Check if each fetched author is already present in the existing authors
            Set<Author> newAuthors = authors.stream()
                    .filter(author -> !finalExistingAuthors.contains(author))
                    .collect(Collectors.toSet());

            // Add the new authors to the book
            existingAuthors.addAll(newAuthors);
            book.setAuthors(existingAuthors);
        } else {
            throw new IllegalArgumentException("authorIds must be an iterable of Long");
        }
    }


    private void mapGenres(Book book, Object genreIds) {
        if (genreIds instanceof Iterable<?>) {
            Iterable<Long> ids = castToIterableOfLong(genreIds);
            Set<Genre> existingGenres = book.getGenres(); // Get the current genres

            // Initialize existingGenres as an empty set if it is null
            if (existingGenres == null) {
                existingGenres = new HashSet<>();
            }

            // Create a final copy of the existingGenres set for reference in lambda expression
            final Set<Genre> finalExistingGenres = existingGenres;

            // Fetch the genres from the database that match the given IDs
            Set<Genre> genres = StreamSupport.stream(ids.spliterator(), false)
                    .map(genreRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            // Check if each fetched genre is already present in the existing genres
            Set<Genre> newGenres = genres.stream()
                    .filter(genre -> !finalExistingGenres.contains(genre))
                    .collect(Collectors.toSet());

            // Add the new genres to the book
            existingGenres.addAll(newGenres);
            book.setGenres(existingGenres);
        } else {
            throw new IllegalArgumentException("genreIds must be an iterable of Long");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Iterable<T> castToIterableOfLong(Object object) {
        return (Iterable<T>) object;
    }

    private Book mapBookRequestToCreateBook(BookRequest bookRequest) {
        Book book = createBookModelMapper.map(bookRequest, Book.class);
        mapPublisher(book, bookRequest.getPublisherId());
        mapAuthors(book, bookRequest.getAuthorIds());
        mapGenres(book, bookRequest.getGenreIds());
        return book;
    }

    private BookDTO mapToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}
