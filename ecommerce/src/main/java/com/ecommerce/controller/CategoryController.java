package com.ecommerce.controller;

import com.ecommerce.entity.Category;
import com.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // POST /api/categories/store/{storeId} : Ajouter une catégorie sur une boutique
    @PostMapping("/store/{storeId}")
    public ResponseEntity<Category> addCategory(@RequestBody Category category, @PathVariable Long storeId) {
        Category newCategory = categoryService.createCategory(category, storeId);
        return ResponseEntity.ok(newCategory);
    }

    // GET /api/categories/store/{storeId} : Récupérer les catégories d'une boutique
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Category>> getStoreCategories(@PathVariable Long storeId) {
        // Cette route contient la variable "storeId", elle sera surveillée par notre intercepteur !
        List<Category> categories = categoryService.getCategoriesByStore(storeId);
        return ResponseEntity.ok(categories);
    }
}