package com.ecommerce.service;

import com.ecommerce.entity.Store;
import com.ecommerce.entity.Theme;
import com.ecommerce.repository.StoreRepository;
import com.ecommerce.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    // Créer une nouvelle boutique avec un thème de couleur par défaut
    @Transactional // Garantit que si la création du thème échoue, la boutique n'est pas créée (Rollback)
    public Store createStore(Store store) {
        // 1. Enregistrer d'abord la boutique pour générer son ID
        Store savedStore = storeRepository.save(store);

        // 2. Créer un thème avec des styles par défaut pour cette nouvelle boutique
        Theme defaultTheme = new Theme();
        defaultTheme.setPrimaryColor("#3f51b5");     // Bleu par défaut
        defaultTheme.setSecondaryColor("#ff4081");   // Rose par défaut
        defaultTheme.setBackgroundColor("#ffffff");  // Blanc par défaut
        defaultTheme.setFontFamily("'Roboto', sans-serif");
        defaultTheme.setStore(savedStore);            // On lie le thème à la boutique créée

        // 3. Enregistrer le thème en base de données
        themeRepository.save(defaultTheme);

        return savedStore;
    }

    // Récupérer une boutique par son ID
    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boutique introuvable"));
    }
}