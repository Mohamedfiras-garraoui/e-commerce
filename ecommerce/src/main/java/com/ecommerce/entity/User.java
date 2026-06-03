package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*; // <-- Pour les Getters, Setters et Constructeurs

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    @Column(nullable = false, unique = true) // Sécurise l'email au niveau de la base
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "store_id") // Doit correspondre à la colonne clé étrangère dans phpMyAdmin
    private Store store;
}