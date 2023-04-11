package com.springboot.boxo.entity;

import com.springboot.boxo.enums.PaymentType;
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
@Table(name = "payments")
public class Payment extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isPaid;

    @Enumerated(EnumType.STRING)
    private PaymentType type = PaymentType.CASH_ON_DELIVERY;

    @Column(nullable = false)
    private float value;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}