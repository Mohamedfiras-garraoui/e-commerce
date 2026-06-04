package com.ecommerce.security;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. Récupérer le token JWT dans l'en-tête de la requête
        String headerAuth = request.getHeader("Authorization");
        String jwt = null;
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            jwt = headerAuth.substring(7);
        }

        // Si pas de token (ex: route publique), on laisse Spring Security gérer la suite
        if (jwt == null) {
            return true;
        }

        // 2. Extraire le storeId de l'utilisateur depuis le JWT
        Long userStoreId = jwtUtils.getStoreIdFromJwtToken(jwt);

        // Si l'utilisateur n'a pas de boutique associée (ex: Super Admin global), il a droit à tout
        if (userStoreId == null) {
            return true; 
        }

        // 3. Intercepter les variables de chemin (ex: si l'URL contient un id de produit)
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        
        if (pathVariables != null && pathVariables.containsKey("id")) {
            try {
                Long productId = Long.parseLong(pathVariables.get("id"));

                // 4. LE CONTRÔLE DE SÉCURITÉ CRUCIAL :
                // On cherche le produit en base pour voir à quel store il appartient
                Product product = productRepository.findById(productId).orElse(null);
                
                if (product != null && product.getStore() != null) {
                    Long productStoreId = product.getStore().getId();
                    
                    // Si l'ID de la boutique du produit ne correspond pas à la boutique de l'utilisateur connecté : BLOQUÉ !
                    if (!productStoreId.equals(userStoreId)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Erreur 403
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Accès refusé: Ce produit n'appartient pas à votre boutique.\"}");
                        return false; // Stoppe immédiatement la requête ici !
                    }
                }
            } catch (NumberFormatException e) {
                // Si l'id dans l'URL n'est pas un nombre, on laisse passer pour que le contrôleur lève sa propre erreur
                return true;
            }
        }

        return true; // Tout est OK, l'utilisateur a le droit d'accéder à son produit
    }
}