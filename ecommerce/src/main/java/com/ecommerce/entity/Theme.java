package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*; // Importation de Lombok pour simplifier le code

@Entity
@Table(name = "themes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "primary_color") // Optionnel : pour être explicite sur le nom en BDD
    private String primaryColor;

    @Column(name = "secondary_color")
    private String secondaryColor;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column(name = "font_family")
    private String fontFamily;

    @OneToOne
    @JoinColumn(name = "store_id") // La clé étrangère qui lie ce style à une boutique précise
    private Store store;
}