package com.ecommerce.controller;

import com.ecommerce.entity.Theme;
import com.ecommerce.service.ThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/themes") // Modifié 
@CrossOrigin(origins = "http://localhost:4200")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    /**
     * 🌐 Récupérer le style d'une boutique spécifique
     * URL Angular : GET http://localhost:8080/api/themes/store/1
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Theme> getThemeByStore(@PathVariable Long storeId) {
        Theme theme = themeService.getThemeByStoreId(storeId);
        return ResponseEntity.ok(theme);
    }

    /**
     * 🛠️ Modifier le style depuis le dashboard vendeur
     * URL Angular : PUT http://localhost:8080/api/themes/store/1
     */
    @PutMapping("/store/{storeId}")
    public ResponseEntity<Theme> updateTheme(@PathVariable Long storeId, @RequestBody Theme themeData) {
        Theme updatedTheme = themeService.updateTheme(storeId, themeData);
        return ResponseEntity.ok(updatedTheme);
    }
}