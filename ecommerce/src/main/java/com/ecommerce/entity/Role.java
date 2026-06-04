package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // <-- C'est cette annotation cruciale qui manquait ou posait problème !
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false, unique = true)
    private ERole name;
}