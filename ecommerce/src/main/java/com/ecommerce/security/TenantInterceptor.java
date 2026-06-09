package com.ecommerce.security;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.Store;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.StoreRepository;
import com.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

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

        String email = jwtUtils.getUserNameFromJwtToken(jwt);
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) {
            System.out.println("TenantInterceptor: No user found for token, allowing request");
            return true;
        }

        Set<Long> ownedStoreIds = storeRepository.findByOwnerId(currentUser.getId()).stream()
                .map(Store::getId)
                .collect(Collectors.toSet());

        Long tokenStoreId = jwtUtils.getStoreIdFromJwtToken(jwt);
        if (tokenStoreId != null) {
            ownedStoreIds.add(tokenStoreId);
        }

        System.out.println("TenantInterceptor: User " + email + " owns store IDs: " + ownedStoreIds);

        if (ownedStoreIds.isEmpty()) {
            System.out.println("TenantInterceptor: User has no stores, allowing request");
            return true;
        }

        // 3. Intercepter les variables de chemin (ex: si l'URL contient un id de produit)
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        System.out.println("TenantInterceptor: Path variables: " + pathVariables);
        
        Long requestedStoreId = null;

        String headerTenant = request.getHeader("X-Tenant-ID");
        if (headerTenant != null && !headerTenant.isBlank()) {
            try {
                requestedStoreId = Long.parseLong(headerTenant);
                System.out.println("TenantInterceptor: Found X-Tenant-ID: " + requestedStoreId);
            } catch (NumberFormatException ignored) {
                requestedStoreId = null;
            }
        }

        if (pathVariables != null && pathVariables.containsKey("storeId")) {
            try {
                requestedStoreId = Long.parseLong(pathVariables.get("storeId"));
                System.out.println("TenantInterceptor: Found storeId in path: " + requestedStoreId);
            } catch (NumberFormatException ignored) {
                requestedStoreId = null;
            }
        }

        if (pathVariables != null && pathVariables.containsKey("id")) {
            try {
                Long resourceId = Long.parseLong(pathVariables.get("id"));
                String requestUri = request.getRequestURI();
                
                // Only check for product if the request is about products
                if (requestUri.contains("/api/products/")) {
                    System.out.println("TenantInterceptor: Found productId in path: " + resourceId);

                    // 4. LE CONTRÔLE DE SÉCURITÉ CRUCIAL :
                    // On cherche le produit en base pour voir à quel store il appartient
                    Product product = productRepository.findById(resourceId).orElse(null);
                    
                    if (product != null && product.getStore() != null) {
                        requestedStoreId = product.getStore().getId();
                        System.out.println("TenantInterceptor: Product " + resourceId + " belongs to store " + requestedStoreId);
                    } else if (product == null) {
                        System.out.println("TenantInterceptor: Product " + resourceId + " not found");
                    }
                } else if (requestUri.contains("/api/stores/")) {
                    System.out.println("TenantInterceptor: Found storeId in path: " + resourceId);
                    // Check if this store ID is in the user's owned stores
                    requestedStoreId = resourceId;
                }
            } catch (NumberFormatException e) {
                // Si l'id dans l'URL n'est pas un nombre, on laisse passer pour que le contrôleur lève sa propre erreur
                return true;
            }
        }

        if (requestedStoreId != null && !ownedStoreIds.contains(requestedStoreId)) {
            System.out.println("TenantInterceptor: User does NOT own store " + requestedStoreId + " - returning 403");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Erreur 403
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Accès refusé: Cette boutique n'appartient pas à votre compte.\"}");
            return false;
        } else {
            System.out.println("TenantInterceptor: Request allowed - requestedStoreId=" + requestedStoreId);
        }

        return true;
    }
}