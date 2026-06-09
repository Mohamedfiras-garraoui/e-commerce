package com.ecommerce.controller;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.SignupRequest;
import com.ecommerce.dto.JwtResponse;
import com.ecommerce.entity.ERole;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.StoreRepository;
import com.ecommerce.security.JwtUtils;
import com.ecommerce.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, 
                                                 userDetails.getId(), 
                                                 userDetails.getFirstname(), 
                                                 userDetails.getLastname(), 
                                                 userDetails.getEmail(),
                                                 userDetails.getStoreId(), 
                                                 roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        logger.info("Received signup request: {}", signUpRequest);
        logger.info("Signup fields - firstname: {}, lastname: {}, email: {}, roles: {}, storeId: {}", 
            signUpRequest.getFirstname(), 
            signUpRequest.getLastname(), 
            signUpRequest.getEmail(), 
            signUpRequest.getRoles(), 
            signUpRequest.getStoreId());
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.warn("Email already exists: {}", signUpRequest.getEmail());
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Cet email est déjà utilisé !"));
        }

        // Création du nouvel utilisateur avec mot de passe encodé
        User user = new User();
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        // Association optionnelle à une boutique (multi-tenant)
        // Association optionnelle à une boutique (multi-tenant)
        if (signUpRequest.getStoreId() != null) {
            storeRepository.findById(signUpRequest.getStoreId().longValue()).ifPresent(user::setStore); // <-- L'ajout de .longValue() règle le problème !
        }

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(adminRole);
                        break;
                    case "merchant":
                        Role merchantRole = roleRepository.findByName(ERole.ROLE_MERCHANT)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(merchantRole);
                        break;
                    default:
                        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                        roles.add(customerRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        logger.info("User saved successfully with ID: {}", savedUser.getId());

        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès !"));
    }
    
    // --- Merchant Management Endpoints ---
    
    // Create a new merchant
    @PostMapping("/merchant")
    public ResponseEntity<?> createMerchant(@RequestBody SignupRequest merchantRequest) {
        logger.info("Received create merchant request: {}", merchantRequest);
        
        if (userRepository.existsByEmail(merchantRequest.getEmail())) {
            logger.warn("Email already exists for merchant: {}", merchantRequest.getEmail());
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Cet email est déjà utilisé !"));
        }

        // Create new merchant user
        User merchant = new User();
        merchant.setFirstname(merchantRequest.getFirstname());
        merchant.setLastname(merchantRequest.getLastname());
        merchant.setEmail(merchantRequest.getEmail());
        merchant.setPassword(encoder.encode(merchantRequest.getPassword()));
        merchant.setStatus(User.UserStatus.ACTIVE); // Default status
        
        // Assign ROLE_MERCHANT
        Set<Role> roles = new HashSet<>();
        Role merchantRole = roleRepository.findByName(ERole.ROLE_MERCHANT)
                .orElseThrow(() -> new RuntimeException("Erreur: Rôle MERCHANT non trouvé."));
        roles.add(merchantRole);
        merchant.setRoles(roles);
        
        if (merchantRequest.getStoreId() != null) {
            storeRepository.findById(merchantRequest.getStoreId().longValue()).ifPresent(merchant::setStore);
        }
        
        User savedMerchant = userRepository.save(merchant);
        logger.info("Merchant saved successfully with ID: {}", savedMerchant.getId());
        
        // Return the created merchant
        return ResponseEntity.ok(new MerchantResponse(savedMerchant));
    }
    
    // Get all merchants
    @GetMapping("/merchants")
    public ResponseEntity<List<MerchantResponse>> getAllMerchants() {
        List<User> merchants = userRepository.findByRoles_Name(ERole.ROLE_MERCHANT);
        List<MerchantResponse> merchantResponses = merchants.stream()
                .map(MerchantResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(merchantResponses);
    }
    
    // Toggle merchant status
    @PutMapping("/merchant/{id}/status")
    public ResponseEntity<?> toggleMerchantStatus(@PathVariable Integer id) {
        User merchant = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erreur: Marchand non trouvé."));
        
        // Toggle status
        if (merchant.getStatus() == User.UserStatus.ACTIVE) {
            merchant.setStatus(User.UserStatus.INACTIVE);
        } else {
            merchant.setStatus(User.UserStatus.ACTIVE);
        }
        
        User updatedMerchant = userRepository.save(merchant);
        return ResponseEntity.ok(new MerchantResponse(updatedMerchant));
    }
    
    // Inner class for JSON responses
    static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    // Inner class for Merchant responses
    static class MerchantResponse {
        private Integer id;
        private String firstname;
        private String lastname;
        private String email;
        private String status;
        
        public MerchantResponse(User user) {
            this.id = user.getId();
            this.firstname = user.getFirstname();
            this.lastname = user.getLastname();
            this.email = user.getEmail();
            this.status = user.getStatus() != null ? user.getStatus().name() : "ACTIVE";
        }
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getFirstname() { return firstname; }
        public void setFirstname(String firstname) { this.firstname = firstname; }
        public String getLastname() { return lastname; }
        public void setLastname(String lastname) { this.lastname = lastname; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}