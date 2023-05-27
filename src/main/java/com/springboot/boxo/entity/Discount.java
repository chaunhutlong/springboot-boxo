package com.springboot.boxo.entity;


import com.springboot.boxo.enums.DiscountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discounts")
public class Discount extends Auditable {
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
    @Enumerated(EnumType.STRING)
    private DiscountType type;
    private double value;
    private double maxValue;
    private double minRequiredValue;
    private Date startDate;
    private Date endDate;
    private int quantity;
    private boolean isActive;
    private boolean isPublic;

    @ManyToMany
    @JoinTable(
            name = "discount_books",
            joinColumns = @JoinColumn(name = "discount_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )

    private Set<Book> books;
}