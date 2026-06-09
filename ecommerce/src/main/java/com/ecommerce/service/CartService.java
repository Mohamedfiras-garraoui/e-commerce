package com.ecommerce.service;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getCartByUser(User user) {
        return cartItemRepository.findByUser(user);
    }

    @Transactional
    public CartItem addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Vérifier si le produit est déjà dans le panier
        List<CartItem> existingItems = cartItemRepository.findByUser(user);
        for (CartItem item : existingItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return cartItemRepository.save(item);
            }
        }

        // Sinon, créer un nouvel article
        CartItem newItem = new CartItem(user, product, quantity);
        return cartItemRepository.save(newItem);
    }

    @Transactional
    public CartItem updateCartItemQuantity(User user, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Article du panier non trouvé"));
        
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Accès non autorisé à cet article");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Article du panier non trouvé"));
        
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Accès non autorisé à cet article");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
