package com.ecommerce.repository;

import com.ecommerce.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    
    // TRÈS IMPORTANT POUR VOTRE PROJET :
    // Permet de récupérer directement le style personnalisé en connaissant l'ID de la boutique
    Optional<Theme> findByStoreId(Long storeId);
}