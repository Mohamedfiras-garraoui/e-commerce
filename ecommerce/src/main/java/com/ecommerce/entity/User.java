package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*; // Pour les Getters, Setters et Constructeurs
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Modifié en Integer pour correspondre au INT de votre phpMyAdmin !

    private String firstname;

    private String lastname;

    @Column(nullable = false, unique = true) 
    private String email;

    @Column(nullable = false)
    private String password;

    // MODIFICATION : Remplacement de l'ancien champ unique par la relation Many-To-Many
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "store_id") 
    private Store store;
}