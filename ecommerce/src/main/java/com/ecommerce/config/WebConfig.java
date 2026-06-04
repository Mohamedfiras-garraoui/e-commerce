package com.ecommerce.config;

import com.ecommerce.security.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // On applique l'intercepteur sur toutes les routes de modification ou consultation de produits
        // Vous pourrez ajouter ici d'autres routes à protéger plus tard (ex: "/api/orders/**")
        registry.addInterceptor(tenantInterceptor)
            .addPathPatterns("/api/products/**")
            .addPathPatterns("/api/orders/**")
            .addPathPatterns("/api/categories/**"); 
    }
}