package com.springboot.boxo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.validation.constraints.Pattern;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Pattern(regexp = "^\\+(?:\\d ?){6,14}\\d$", message = "Invalid phone number")
    private String phone;
    private float distance;
    boolean isDefault = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}
