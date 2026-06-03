package com.ecommerce.repository;

import com.ecommerce.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    // Cette méthode permettra de chercher une boutique par son email si besoin
    Optional<Store> findByEmail(String email);
}