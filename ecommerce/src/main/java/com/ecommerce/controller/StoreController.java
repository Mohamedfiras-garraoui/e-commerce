package com.ecommerce.controller;

import com.ecommerce.entity.Store;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/stores") // Modifié 
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 🏢 Créer une nouvelle boutique (avec son thème par défaut automatique)
     * URL Angular : POST http://localhost:8080/api/stores
     */
    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
        logger.info("Creating store with request: {}", store);
        // Ensure any client-provided IDs are ignored to force a creation (avoid accidental updates)
        if (store.getId() != null) {
            store.setId(null);
        }
        if (store.getTheme() != null && store.getTheme().getId() != null) {
            store.getTheme().setId(null);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (store.getOwner() == null) {
            store.setOwner(currentUser);
        }

        Store newStore = storeService.createStore(store);
        logger.info("Created store: {}", newStore);
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
    
    /**
     * 🔍 Récupérer toutes les boutiques d'un utilisateur
     * URL Angular : GET http://localhost:8080/api/stores/user/1
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Store>> getStoresByUserId(@PathVariable Integer userId) {
        logger.info("Fetching stores for user ID: {}", userId);
        List<Store> stores = storeService.getStoresByOwnerId(userId);
        logger.info("Found {} stores for user ID: {}", stores.size(), userId);
        for (Store store : stores) {
            logger.info("Store - ID: {}, Name: {}, Domain: {}", store.getId(), store.getName(), store.getDomain());
        }
        return ResponseEntity.ok(stores);
    }
    
    /**
     * 🔄 Mettre à jour une boutique
     * URL Angular : PUT http://localhost:8080/api/stores/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody Store storeDetails) {
        Store updatedStore = storeService.updateStore(id, storeDetails);
        return ResponseEntity.ok(updatedStore);
    }

    /**
     * 🏪 Récupérer toutes les boutiques disponibles (pour les clients)
     * URL Angular : GET http://localhost:8080/api/stores
     */
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        logger.info("Fetching all stores");
        List<Store> stores = storeService.getAllStores();
        logger.info("Found {} stores", stores.size());
        return ResponseEntity.ok(stores);
    }

    /**
     * 🗑️ Supprimer une boutique
     * URL Angular : DELETE http://localhost:8080/api/stores/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}