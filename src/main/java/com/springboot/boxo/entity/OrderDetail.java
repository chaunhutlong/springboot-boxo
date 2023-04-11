package com.springboot.boxo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractAuditable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private float price;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
