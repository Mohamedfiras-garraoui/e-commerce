package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService; // <-- AJOUT : Injection du service d'avis

    /**
     * ➕ Ajouter un nouveau produit
     * POST http://localhost:8080/api/products
     */
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product newProduct = productService.addProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    /**
     * 🏪 Récupérer les produits d'une boutique donnée
     * GET http://localhost:8080/api/products/store/1
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Product>> getProductsByStore(@PathVariable Long storeId) {
        List<Product> products = productService.getProductsByStore(storeId);
        return ResponseEntity.ok(products);
    }

    /**
     * 🔍 Récupérer un produit par son ID avec sa note moyenne
     * GET http://localhost:8080/api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        // 1. Récupération du produit via le service existant
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Produit non trouvé\"}");
        }
                
        // 2. Calcul de la note moyenne en temps réel via le ReviewService
        double averageRating = reviewService.getAverageRating(id);
        
        // 3. Construction d'une réponse enrichie sous forme de Map (JSON)
        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("averageRating", averageRating);
        
        return ResponseEntity.ok(response);
    }
}