package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    // Correction : Retrait de unique=true pour permettre à plusieurs boutiques d'avoir les mêmes noms de catégories
    @Column(nullable = false)
    private String name;

    // <-- AJOUT MULTI-TENANT : Chaque catégorie appartient à une boutique spécifique
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Évite les soucis de proxies JPA lors de la sérialisation
    private Store store;

    // Optionnel : Permet de voir tous les produits de cette catégorie si besoin
    // @JsonIgnore évite une boucle infinie lors de la sérialisation JSON
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}