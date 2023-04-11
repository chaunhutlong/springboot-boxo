package com.springboot.boxo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String avatar;

    // relationships one to many discounts and one to many orders
    @OneToOne(mappedBy = "profile")
    private User user;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<Address> addresses;

}
