package com.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Set<String> roles; // Permet de passer ["customer"] ou ["merchant"]
    private Integer storeId;  // Optionnel, utile si c'est un marchand ou client lié à une boutique
}