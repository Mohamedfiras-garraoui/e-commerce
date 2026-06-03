package com.ecommerce.service;

import com.ecommerce.entity.Theme;
import com.ecommerce.repository.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    // 1. Récupérer le thème d'une boutique spécifique par son ID
    public Theme getThemeByStoreId(Long storeId) {
        return themeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new RuntimeException("Thème introuvable pour la boutique avec l'ID : " + storeId));
    }

    // 2. Mettre à jour ou enregistrer un thème personnalisé
    public Theme updateTheme(Long storeId, Theme newThemeData) {
        // On vérifie d'abord si le thème existe déjà
        Theme existingTheme = themeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new RuntimeException("Thème introuvable pour cette boutique"));

        // On applique les nouvelles modifications de style
        existingTheme.setPrimaryColor(newThemeData.getPrimaryColor());
        existingTheme.setSecondaryColor(newThemeData.getSecondaryColor());
        existingTheme.setBackgroundColor(newThemeData.getBackgroundColor());
        existingTheme.setFontFamily(newThemeData.getFontFamily());

        // On sauvegarde les changements dans la base de données
        return themeRepository.save(existingTheme);
    }
}