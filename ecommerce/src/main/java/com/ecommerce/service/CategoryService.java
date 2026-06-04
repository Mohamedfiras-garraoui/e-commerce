package com.ecommerce.service;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Store;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    // Créer une nouvelle catégorie pour une boutique
    public Category createCategory(Category category, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));
        category.setStore(store);
        return categoryRepository.save(category);
    }

    // Récupérer toutes les catégories d'une boutique spécifique
    public List<Category> getCategoriesByStore(Long storeId) {
        return categoryRepository.findByStoreId(storeId);
    }
}