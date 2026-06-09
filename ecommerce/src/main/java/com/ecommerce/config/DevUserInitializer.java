package com.ecommerce.config;

import com.ecommerce.entity.ERole;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DevUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Vérifier si l'utilisateur de test existe déjà
        if (userRepository.existsByEmail("test@example.com")) {
            return;
        }

        // 2. Récupérer le rôle ROLE_CUSTOMER via l'Enum ERole, ou le créer s'il n'existe pas en BDD
        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(ERole.ROLE_CUSTOMER); // Utilise l'Enum ERole ici au lieu d'une String
                    return roleRepository.save(newRole);
                });

        // 3. Création de l'utilisateur de test
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");

        // Hachage du mot de passe avec BCrypt (indispensable pour Spring Security)
        user.setPassword(passwordEncoder.encode("password123"));

        // 4. Assigner le rôle à l'utilisateur
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);

        // 5. Sauvegarder l'utilisateur final en base de données
        userRepository.save(user);
        System.out.println(">> Utilisateur de dev créé avec succès (test@example.com / password123)");
    }
}