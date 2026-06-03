package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false) // Ajouté : correspond à la nouvelle colonne SQL
    private Integer stock;

    @Column(name = "image") // Modifié : imageUrl devient image pour correspondre à votre BDD
    private String image;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // Ajouté : gère la colonne category_id présente dans votre table MySQL
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}