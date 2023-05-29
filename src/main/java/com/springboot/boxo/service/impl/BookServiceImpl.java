package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.*;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.exception.ResourceNotFoundException;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.BookDTO;
import com.springboot.boxo.payload.request.BookCrawlRequest;
import com.springboot.boxo.payload.request.BookRequest;
import com.springboot.boxo.repository.*;
import com.springboot.boxo.service.BookService;
import com.springboot.boxo.service.StorageService;
import com.springboot.boxo.utils.PaginationUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static java.util.function.Predicate.not;

@Service
public class BookServiceImpl implements BookService {
    @Value("${google.book.api.key}")
    private String googleBookApiKey;
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

    @Override
    public List<BookDTO> crawlBooks(BookCrawlRequest body) {
        try {
            String genre = body.getGenre();
            String lang = body.getLang();
            String query = body.getKeyword();
            if (genre != null) {
                query += "+subject:" + genre;
            }

            String baseUrl = "https://www.googleapis.com/books/v1/volumes";
            int maxResults = 40;

            // Make the initial API request to get the total number of items
            String initialUrl = String.format("%s?q=%s&key=%s&printType=books&langRestrict=%s", baseUrl, query, googleBookApiKey, lang);
            JSONObject initialResponse = makeRequest(initialUrl);
            int totalItems = initialResponse.optInt("totalItems", 0);

            // Calculate the number of requests needed based on totalItems
            int maxIndex = (int) Math.ceil((double) totalItems / maxResults);

            // Create an array of start indices
            List<Integer> startIndices = IntStream.range(0, maxIndex)
                    .map(index -> index * maxResults)
                    .boxed()
                    .toList();

            // Execute the API requests concurrently using CompletableFuture and map
            String finalQuery = query;
            List<CompletableFuture<List<Book>>> bookFutures = startIndices.stream()
                    .map(startIndex -> CompletableFuture.supplyAsync(() -> {
                        String url = String.format("%s?q=%s&key=%s&printType=books&langRestrict=%s&startIndex=%d&maxResults=%d",
                                baseUrl, finalQuery, googleBookApiKey, lang, startIndex, maxResults);
                        JSONObject response = makeRequest(url);
                        JSONArray items = response.optJSONArray("items");
                        return processBooks(items);
                    }))
                    .toList();

            // Wait for all the CompletableFuture to complete and collect the results
            List<Book> books = bookFutures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Collection::stream)
                    .toList();

            return books.stream()
                    .map(this::mapToDTO)
                    .toList();

        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private JSONObject makeRequest(String url) {
        // Implement the logic to make the API request and return the response as a JSONObject
        // You can use libraries like HttpClient or RestTemplate for this purpose
        // Example implementation using HttpClient:
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            return new JSONObject(responseString);
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to make API request");
        }
    }

    private List<Book> processBooks(JSONArray items) {
        if (items == null) {
            return new ArrayList<>();
        }
        List<Book> books = new ArrayList<>();

        IntStream.range(0, items.length()).mapToObj(items::getJSONObject).forEach(item -> {
            JSONObject volumeInfo = item.getJSONObject("volumeInfo");
            JSONObject volumeSaleInfo = item.optJSONObject("saleInfo");
            if (volumeInfo == null || volumeSaleInfo == null) {
                return;
            }
            String isbn = extractIsbn(volumeInfo.optJSONArray("industryIdentifiers"));
            if (isbn == null) {
                return;
            }
            var bookInDatabase = bookRepository.findByIsbn(isbn);
            if (bookInDatabase.isPresent()) {
                return;
            }
            Book book = createBookFromVolumeInfo(volumeInfo, volumeSaleInfo, isbn);
            books.add(book);
        });

        return books;
    }

    private Book createBookFromVolumeInfo(JSONObject volumeInfo, JSONObject volumeSaleInfo, String isbn) {
        System.out.println("Creating book from volume info");
        String title = volumeInfo.optString("title");
        JSONArray authorsArray = volumeInfo.optJSONArray("authors");
        Set<String> authors = extractAuthors(authorsArray);
        String publisher = volumeInfo.optString("publisher");
        String description = volumeInfo.optString("description");
        int pageCount = volumeInfo.optInt("pageCount");
        JSONArray categoriesArray = volumeInfo.optJSONArray("categories");
        Set<String> categories = extractCategories(categoriesArray);
        String thumbnail = extractThumbnail(volumeInfo.optJSONObject("imageLinks"));
        String language = volumeInfo.optString("language");
        String publishedDate = volumeInfo.optString("publishedDate");
        double listPrice = extractListPrice(volumeSaleInfo);
        double retailPrice = extractRetailPrice(volumeSaleInfo);
        double calculatedPrice;
        if (listPrice != 0) {
            calculatedPrice = listPrice;
        } else if (retailPrice != 0) {
            calculatedPrice = retailPrice;
        } else {
            calculatedPrice = Math.floor(Math.random() * 1000) * 1000;
        }

        if (retailPrice <= 0) {
            retailPrice = calculatedPrice;
        }

        Set<Genre> genresSet = findGenresFromCategories(categories, title);

        Set<Author> authorsSet = findOrCreateAuthors(authors);

        Publisher publisherInDatabase = findOrCreatePublisher(publisher);

        Book book = new Book();
        book.setName(title);
        book.setIsbn(isbn);
        if (!authorsSet.isEmpty()) {
            book.setAuthors(authorsSet);
        }
        book.setPublisher(publisherInDatabase);
        book.setDescription(description);
        book.setTotalPages(pageCount);
        if (!genresSet.isEmpty()) {
            book.setGenres(genresSet);
        }
        book.setAvailableQuantity(1000);
        book.setLanguage(language);
        book.setPublishedDate(publishedDate);
        book.setPrice(calculatedPrice);
        book.setPriceDiscount(retailPrice);
        bookRepository.save(book);
        saveBookImage(thumbnail, book);

        System.out.println("Returning book");
        return book;
    }

    private Set<String> extractAuthors(JSONArray authorsArray) {
        if (authorsArray != null && authorsArray.length() > 0) {
            return new HashSet<>(Arrays.asList(toStringArray(authorsArray)));
        }

        return Collections.emptySet();
    }

    private String extractIsbn(JSONArray industryIdentifiers) {
        return industryIdentifiers != null ? industryIdentifiers.getJSONObject(0).optString("identifier") : null;
    }

    private Set<String> extractCategories(JSONArray categoriesArray) {
        if (categoriesArray != null && categoriesArray.length() > 0) {
            return new HashSet<>(Arrays.asList(toStringArray(categoriesArray)));
        }

        return Collections.emptySet();
    }

    private String extractThumbnail(JSONObject imageLinks) {
        return imageLinks != null ? imageLinks.optString("thumbnail") : null;
    }

    private double extractListPrice(JSONObject volumeSaleInfo) {
        double listPrice = 0.0;

        if (volumeSaleInfo != null) {
            JSONObject listPriceObj = volumeSaleInfo.optJSONObject("listPrice");
            if (listPriceObj != null) {
                listPrice = listPriceObj.optDouble("amount");
            }
        }

        return listPrice;
    }

    private double extractRetailPrice(JSONObject volumeSaleInfo) {
        double retailPrice = 0.0;

        if (volumeSaleInfo != null) {
            JSONObject retailPriceObj = volumeSaleInfo.optJSONObject("retailPrice");
            if (retailPriceObj != null) {
                retailPrice = retailPriceObj.optDouble("amount");
            }
        }

        return retailPrice;
    }

    private Set<Genre> findGenresFromCategories(Set<String> categories, String title) {
        HashSet<Genre> genresSet = new HashSet<>();

        for (String category : categories) {
            List<Genre> genres = genreRepository.findTopBySearchTerm(category, PageRequest.of(0, 1));
            if (genres.isEmpty()) {
                List<Genre> genresByTitle = genreRepository.findByBookTitleContainingGenres(title);
                if (!genresByTitle.isEmpty()) {
                    genresSet.addAll(genresByTitle);
                } else {
                    genreRepository.findTopBySearchTerm("Other", PageRequest.of(0, 1))
                            .stream()
                            .findFirst().ifPresent(genresSet::add);
                }
            } else {
                genresSet.add(genres.get(0));
            }
        }
        return genresSet;
    }

    private Set<Author> findOrCreateAuthors(Set<String> authors) {
        Set<Author> authorsSet = new HashSet<>();
        for (String author : authors) {
            Author authorInDatabase = authorRepository.findByName(author);
            if (authorInDatabase == null) {
                Author newAuthor = new Author();
                newAuthor.setName(author);
                authorsSet.add(newAuthor);
                authorRepository.save(newAuthor);
            } else {
                authorsSet.add(authorInDatabase);
            }
        }
        return authorsSet;
    }

    private Publisher findOrCreatePublisher(String publisherName) {
        Publisher publisherInDatabase = publisherRepository.findByName(publisherName);
        if (publisherInDatabase == null) {
            Publisher newPublisher = new Publisher();
            newPublisher.setName(publisherName);
            publisherRepository.save(newPublisher);
            publisherInDatabase = newPublisher;
        }
        return publisherInDatabase;
    }

    private void saveBookImage(String thumbnail, Book book) {
        if (thumbnail != null) {
            BookImage bookImage = new BookImage();
            bookImage.setUrl(thumbnail);
            bookImage.setBook(book);
            bookImageRepository.save(bookImage);
        }
    }

    private String[] toStringArray(JSONArray jsonArray) {
        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            array[i] = jsonArray.optString(i);
        }
        return array;
    }

    @Override
    public PaginationResponse<BookDTO> getAllBooks(String searchTerm, int pageNumber, int pageSize, String sortBy, String sortDir) {
        try {

            if (sortBy == null || sortBy.isEmpty()) {
                List<Book> books = bookRepository.searchBooks(searchTerm);
                List<BookDTO> content = books.stream().map(this::mapToDTO).toList();
                return PaginationUtils.createPaginationResponse(content, books.size(), pageNumber, pageSize);
            }

            Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
            Page<Book> books = bookRepository.searchBooks(searchTerm, pageable);
            List<BookDTO> content = books.getContent().stream().map(this::mapToDTO).toList();

            return PaginationUtils.createPaginationResponse(content, books);
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
