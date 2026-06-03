package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Indispensable plus tard pour la connexion (Spring Security) : 
    // Permet de vérifier si l'email existe et de récupérer l'utilisateur avec son mot de passe
    Optional<User> findByEmail(String email);
}