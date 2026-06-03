package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * 📦 Ajouter un produit dans une boutique
     */
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * 🔍 Récupérer tous les produits d'une boutique spécifique
     */
    public List<Product> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId);
    }
}