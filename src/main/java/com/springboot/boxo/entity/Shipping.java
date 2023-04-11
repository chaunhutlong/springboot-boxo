package com.springboot.boxo.entity;

import com.springboot.boxo.enums.ShippingStatus;
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
@Table(name = "shippings")
public class Shipping extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;

    @Column(nullable = false)
    private float value;
    private String description;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}
