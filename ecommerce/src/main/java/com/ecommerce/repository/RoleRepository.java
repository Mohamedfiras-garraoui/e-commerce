package com.ecommerce.repository;

import com.ecommerce.entity.ERole;
import com.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Cette méthode permettra de chercher un rôle par son nom (ex: ROLE_CUSTOMER)
    Optional<Role> findByName(ERole name);
}