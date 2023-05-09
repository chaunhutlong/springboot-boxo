package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.*;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.BookCreator;
import com.springboot.boxo.payload.BookDTO;
import com.springboot.boxo.repository.AuthorRepository;
import com.springboot.boxo.repository.BookRepository;
import com.springboot.boxo.repository.GenreRepository;
import com.springboot.boxo.repository.PublisherRepository;
import com.springboot.boxo.service.BookService;
import com.springboot.boxo.service.StorageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookServiceImpl implements BookService {
    private static final String S3_BUCKET_NAME = "boxo-java";
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, PublisherRepository publisherRepository, GenreRepository genreRepository, AuthorRepository authorRepository, ModelMapper modelMapper, StorageService storageService) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
    }

    @Override
    public HttpStatus createBook(@ModelAttribute BookCreator bookCreator)  {
        try {
                Book book = mapToEntity(bookCreator);
                uploadBookImages(book, bookCreator.getImages());

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

    private void uploadBookImages(Book book, List<String> images) {
        if (images != null && !images.isEmpty()) {
            List<BookImage> bookImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String imageKey = book.getIsbn() + "_" + i;
                storageService.uploadBase64ToS3(S3_BUCKET_NAME, images.get(i), imageKey);
                BookImage bookImage = new BookImage();
                bookImage.setImageKey(imageKey);
                bookImage.setBook(book);
                bookImages.add(bookImage);
            }
            book.setImages(bookImages);
        }
    }

    private Book mapToEntity(BookCreator bookCreator) {
        Book book = modelMapper.map(bookCreator, Book.class);
        mapPublisher(book, bookCreator.getPublisherId());
        mapAuthors(book, bookCreator.getAuthorIds());
        mapGenres(book, bookCreator.getGenreIds());
        return book;
    }

    private void mapPublisher(Book book, Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", "id", publisherId));
        book.setPublisher(publisher);
    }

    private void mapAuthors(Book book, Object authorIds) {
        if (authorIds instanceof Iterable<?>) {
            Iterable<Long> ids = (Iterable<Long>) authorIds;
            Set<Author> authors = StreamSupport.stream(ids.spliterator(), false)
                    .map(authorRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            book.setAuthors(authors);
        } else {
            throw new IllegalArgumentException("authorIds must be an iterable of Long");
        }
    }

    private void mapGenres(Book book, Object genreIds) {
        if (genreIds instanceof Iterable<?>) {
            Iterable<Long> ids = (Iterable<Long>) genreIds;
            Set<Genre> genres = StreamSupport.stream(ids.spliterator(), false)
                    .map(genreRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        } else {
            throw new IllegalArgumentException("genreIds must be an iterable of Long");
        }
    }

    private BookDTO mapToDto(Book book) {
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
        bookDTO.setPublisherId(book.getPublisher().getId());
        bookDTO.setAuthorIds(book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet()));
        bookDTO.setGenreIds(book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        return bookDTO;
    }
}
