package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.*;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookRequest;
import com.springboot.boxo.repository.*;
import com.springboot.boxo.service.BookService;
import com.springboot.boxo.service.StorageService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.function.Predicate.not;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final GenreRepository genreRepository;
    private final AuthorRepository authorRepository;
    private final BookImageRepository bookImageRepository;
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    private final ModelMapper createBookModelMapper;
    private final ModelMapper updateBookModelMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, PublisherRepository publisherRepository, GenreRepository genreRepository, AuthorRepository authorRepository, BookImageRepository bookImageRepository, ModelMapper modelMapper, StorageService storageService) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
        this.genreRepository = genreRepository;
        this.authorRepository = authorRepository;
        this.bookImageRepository = bookImageRepository;
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
                map().setId(null);
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
public PaginationResponse<BookDTO> getAllBooks(int pageNumber, int pageSize, String sortBy, String sortDir) {
        try {
            Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
            Page<Book> books = bookRepository.findAll(pageable);

            List<Book> listOfBooks = books.getContent();
            List<BookDTO> content = listOfBooks.stream().map(this::mapToDTO).toList();

            return PaginationUtils.createPaginationResponse(content, books);
        } catch (Exception e) {
            HttpStatus  statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e instanceof CustomException customException) {
                statusCode = customException.getStatusCode();
            }
            throw new CustomException(statusCode, e.getMessage());
        }
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
            // if images start https then not do anything
            if (bookRequest.getImages() != null && !bookRequest.getImages().isEmpty()) {
                List<String> images = new ArrayList<>();
                Predicate<String> predicate = not(image -> image.startsWith("https"));
                for (String s : bookRequest.getImages()) {
                    if (predicate.test(s)) {
                        images.add(s);
                    }
                }
                if (!images.isEmpty()) {
                    // images is base64string now
                    deleteBookImages(book);
                    uploadBookImages(book, images);
                }
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

    @Override
    public HttpStatus deleteBookById(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
            deleteBookImages(book);
            bookRepository.delete(book);
            return HttpStatus.OK;
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
            if (images.size() == 2 && (!images.get(0).contains(","))) {
                String combinedImage = images.get(0) + "," + images.get(1);
                images = List.of(combinedImage);
            }
            List<BookImage> bookImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String filename = book.getIsbn() + "_" + i;
                Map<String, String> uploadResult = storageService.uploadBase64ToS3(images.get(i), filename);

                BookImage bookImage = new BookImage();
                bookImage.setKey(uploadResult.get("key"));
                bookImage.setUrl(uploadResult.get("url"));
                bookImage.setBook(book);
                bookImages.add(bookImage);
            }
            book.setImages(bookImages);
        }
    }

    private void deleteBookImages(Book book) {
        if (book.getImages() != null && !book.getImages().isEmpty()) {
            // Collect image keys to be deleted
            List<String> imageKeys = book.getImages().stream()
                    .map(BookImage::getKey)
                    .toList();

            storageService.deleteImagesFromS3(imageKeys);

            book.getImages().clear();

            bookImageRepository.deleteByBookId(book.getId());
        }
    }

    private void mapBookRequestToBook(BookRequest bookRequest, Book book) {
        if (bookRequest.getPriceDiscount() == null) {
            book.setPriceDiscount(bookRequest.getPrice());
        }
        Long publisherId = bookRequest.getPublisherId();
        List<Long> authorIds = bookRequest.getAuthors();
        List<Long> genreIds = bookRequest.getGenres();

        Optional.ofNullable(publisherId).ifPresent(pubId -> mapPublisher(book, pubId));
        Optional.ofNullable(authorIds).ifPresent(ids -> mapAuthors(book, ids));
        Optional.ofNullable(genreIds).ifPresent(ids -> mapGenres(book, ids));
    }

    private Book mapBookRequestToCreateBook(BookRequest bookRequest) {
        Book book = createBookModelMapper.map(bookRequest, Book.class);
        mapBookRequestToBook(bookRequest, book);
        return book;
    }

    private void mapBookRequestToUpdateBook(BookRequest bookRequest, Book book) {
        mapBookRequestToBook(bookRequest, book);
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

            // Remove the authors that are not in the given IDs
            existingAuthors.removeIf(author -> !authors.contains(author));

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

            // Remove the genres that are not in the new genres
            existingGenres.removeIf(genre -> !genres.contains(genre));
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

    private BookDTO mapToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}
