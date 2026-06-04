package com.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer id; // Remis en Integer
    private String firstname;
    private String lastname;
    private String email;
    private Integer storeId; // Remis en Integer
    private List<String> roles;

    public JwtResponse(String accessToken, Integer id, String firstname, String lastname, String email, Integer storeId, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.storeId = storeId;
        this.roles = roles;
    }
}