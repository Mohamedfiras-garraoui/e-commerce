package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository; // <-- AJOUT : Pour la validation de sécurité multi-tenant

    /**
     * 📦 Ajouter un produit dans une boutique
     * Amélioration : Validation que le store associé existe bien
     */
    public Product addProduct(Product product) {
        if (product.getStore() == null || product.getStore().getId() == null) {
            throw new RuntimeException("Impossible d'ajouter un produit sans boutique associée.");
        }
        
        // Vérification de sécurité : est-ce que le store existe en base de données ?
        storeRepository.findById(product.getStore().getId())
                .orElseThrow(() -> new RuntimeException("La boutique spécifiée n'existe pas."));

        return productRepository.save(product);
    }

    /**
     * 🔍 Récupérer tous les produits d'une boutique spécifique
     */
    public List<Product> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId);
    }

    /**
     * 🔍 Récupérer un produit spécifique par son ID
     * <-- AJOUT : Requis pour faire fonctionner le calcul des notes moyennes dans le ProductController
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    
    /**
     * 📝 Mettre à jour un produit
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        if (product == null) {
            throw new RuntimeException("Produit non trouvé");
        }
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setImage(productDetails.getImage());
        product.setCategory(productDetails.getCategory());
        return productRepository.save(product);
    }
    
    /**
     * 🗑️ Supprimer un produit
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}