package com.ecommerce.controller;

import com.ecommerce.entity.Store;
import com.ecommerce.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores") // Modifié 
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    @Autowired
    private StoreService storeService;

    /**
     * 🏢 Créer une nouvelle boutique (avec son thème par défaut automatique)
     * URL Angular : POST http://localhost:8080/api/stores
     */
    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
        Store newStore = storeService.createStore(store);
        // Utilisation de CREATED (21) au lieu de OK (200) pour une création de ressource
        return new ResponseEntity<>(newStore, HttpStatus.CREATED);
    }

    /**
     * 🔍 Récupérer les infos d'une boutique par son ID
     * URL Angular : GET http://localhost:8080/api/stores/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long id) {
        Store store = storeService.getStoreById(id);
        return ResponseEntity.ok(store);
    }
}