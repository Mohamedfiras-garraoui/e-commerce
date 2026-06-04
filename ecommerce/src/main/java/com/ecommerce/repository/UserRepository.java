package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Pour trouver un utilisateur par son email lors du Login
    Optional<User> findByEmail(String email);

    // Pour vérifier si l'email existe déjà lors du Signup (C'est ce qui manque !)
    Boolean existsByEmail(String email);
}