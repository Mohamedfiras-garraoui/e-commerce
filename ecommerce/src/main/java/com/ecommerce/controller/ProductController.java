package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    private ProductService productService;

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
}