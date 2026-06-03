package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Optionnel : Permet de voir tous les produits de cette catégorie si besoin
    // @JsonIgnore évite une boucle infinie lors de la sérialisation JSON
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}