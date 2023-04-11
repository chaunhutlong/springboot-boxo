package com.springboot.boxo.entity;


import com.springboot.boxo.enums.DiscountType;
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
@Table(name = "discounts")
public class Discount extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, unique = true)
    private String code;
    private float value;
    private float maxValue;
    private float minRequiredValue;
    private String startDate;
    private String endDate;
    private int maxUses;
    private int uses;
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "discount_books",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books;
}