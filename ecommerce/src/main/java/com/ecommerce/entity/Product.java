package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @Column(nullable = false)
    private Integer stock;


    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnore
    private Store store;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "store", "products"})
    private Category category;
    
    // Helper method to get categoryId for JSON
    @JsonProperty("categoryId")
    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }
}