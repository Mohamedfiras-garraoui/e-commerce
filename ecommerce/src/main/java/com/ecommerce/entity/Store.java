package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*; // <-- Ne pas oublier d'importer Lombok

@Entity
@Table(name = "stores")
@Getter // <-- Génère automatiquement les Getters (Lombok)
@Setter // <-- Génère automatiquement les Setters (Lombok)
@NoArgsConstructor // <-- Génère le constructeur sans argument (Obligatoire pour JPA)
@AllArgsConstructor // <-- Génère le constructeur avec tous les arguments
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String logo;

    private String status;

    // 🔗 LA LIAISON AVEC LE THÈME (INDISPENSABLE POUR VOTRE PROJET)
    // Cela permet de dire à Spring que la boutique est liée à son style personnalisé
    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Theme theme;
}