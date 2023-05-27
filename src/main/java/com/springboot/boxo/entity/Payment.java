package com.springboot.boxo.entity;

import com.springboot.boxo.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentType type = PaymentType.CASH_ON_DELIVERY;

    @Column(nullable = false)
    private double value;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}