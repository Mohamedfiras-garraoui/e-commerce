package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.Store;
import com.ecommerce.entity.Category;
import com.ecommerce.repository.StoreRepository;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Map<String, Object> productData) {
        logger.info("Received product data: {}", productData);
        
        // Extract data
        String name = (String) productData.get("name");
        Number priceNum = (Number) productData.get("price");
        Integer stock = (Integer) productData.get("stock");
        String description = (String) productData.get("description");
        String image = (String) productData.get("image");
        Number categoryIdNum = (Number) productData.get("categoryId");
        Map<String, Object> storeObj = (Map<String, Object>) productData.get("store");
        
        Long storeId = storeObj != null ? ((Number) storeObj.get("id")).longValue() : null;
        Long categoryId = categoryIdNum != null ? categoryIdNum.longValue() : null;
        
        // Fetch store from DB
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new RuntimeException("Store not found"));

        // Create product
        Product product = new Product();
        product.setName(name);
        product.setPrice(priceNum != null ? new java.math.BigDecimal(priceNum.toString()) : java.math.BigDecimal.ZERO);
        product.setStock(stock != null ? stock : 0);
        product.setDescription(description);
        product.setImage(image);
        product.setStore(store);
        
        // Set category if provided
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            product.setCategory(category);
        }
        
        Product newProduct = productService.addProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Product>> getProductsByStore(@PathVariable Long storeId) {
        logger.info("Getting products for store ID: {}", storeId);
        List<Product> products = productService.getProductsByStore(storeId);
        logger.info("Found {} products", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"message\": \"Produit non trouvé\"}");
        }
                
        double averageRating = reviewService.getAverageRating(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("averageRating", averageRating);
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> productData) {
        logger.info("Updating product ID {} with data: {}", id, productData);
        
        // Extract data
        String name = (String) productData.get("name");
        Number priceNum = (Number) productData.get("price");
        Integer stock = (Integer) productData.get("stock");
        String description = (String) productData.get("description");
        String image = (String) productData.get("image");
        Number categoryIdNum = (Number) productData.get("categoryId");
        Map<String, Object> storeObj = (Map<String, Object>) productData.get("store");
        
        Long categoryId = categoryIdNum != null ? categoryIdNum.longValue() : null;
        
        // Get existing product
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Update product fields
        product.setName(name);
        product.setPrice(priceNum != null ? new java.math.BigDecimal(priceNum.toString()) : java.math.BigDecimal.ZERO);
        product.setStock(stock != null ? stock : 0);
        product.setDescription(description);
        product.setImage(image);
        
        // Set category if provided
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}