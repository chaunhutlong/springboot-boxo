package com.springboot.boxo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books", indexes = {
        @Index(name = "isbn_index", columnList = "isbn", unique = true)
})
public class Book extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String isbn;
    private String publicationDate;
    private String language;
    private String description;
    private int pages;
    private int quantity;
    private float price;
    private float priceDiscount;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @ManyToMany
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookImage> images;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Review> reviews;
}
