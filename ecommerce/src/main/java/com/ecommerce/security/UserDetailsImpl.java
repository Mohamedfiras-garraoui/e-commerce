package com.ecommerce.security;

import com.ecommerce.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private Integer id; // Remis en Integer
    private String firstname;
    private String lastname;
    private String email;
    @JsonIgnore
    private String password;
    private Integer storeId; // Remis en Integer

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String firstname, String lastname, String email, String password, Integer storeId,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.storeId = storeId;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        // Si l'id de votre boutique dans Store.java est un Long, on le convertit en Integer avec .intValue(), sinon laissez user.getStore().getId()
        Integer sId = null;
        if (user.getStore() != null) {
            sId = user.getStore().getId().intValue(); // <-- L'ajout de .intValue() règle l'erreur !
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                sId,
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    public Integer getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
    public Integer getStoreId() { return storeId; }

    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}