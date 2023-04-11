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
@Table(name = "publishers")
public class Publisher extends AbstractAuditable<User, Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String address;
    @Pattern(regexp = "^\\+(?:\\d ?){6,14}\\d$", message = "Invalid phone number")
    private String phone;

    // validate email
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email")
    private String email;
}
