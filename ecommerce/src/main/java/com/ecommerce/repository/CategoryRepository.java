package com.ecommerce.repository;

import com.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // <-- AJOUT MULTI-TENANT : Permet de récupérer uniquement les catégories d'une boutique spécifique
    List<Category> findByStoreId(Long storeId);
}