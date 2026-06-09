package com.ecommerce.controller;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        User user = getCurrentUser();
        List<CartItem> cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(@RequestBody Map<String, Object> request) {
        User user = getCurrentUser();
        Long productId = ((Number) request.get("productId")).longValue();
        int quantity = (Integer) request.getOrDefault("quantity", 1);
        
        CartItem cartItem = cartService.addToCart(user, productId, quantity);
        return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        User user = getCurrentUser();
        int quantity = (Integer) request.get("quantity");
        
        CartItem cartItem = cartService.updateCartItemQuantity(user, id, quantity);
        if (cartItem == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        User user = getCurrentUser();
        cartService.removeFromCart(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        User user = getCurrentUser();
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
