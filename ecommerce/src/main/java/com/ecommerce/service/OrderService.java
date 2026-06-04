package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order checkout(User user) {
        // 1. Récupérer les éléments du panier du client
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Le panier est vide ! Impossible de commander.");
        }

        // Pour notre architecture multi-tenant simple, on considère que le panier contient 
        // les articles d'une même boutique. On extrait le Store depuis le premier produit.
        Store store = cartItems.get(0).getProduct().getStore();

        // 2. Initialiser la commande principale
        Order order = new Order();
        order.setUser(user);
        order.setStore(store);
        order.setOrderReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        java.math.BigDecimal globalTotal = java.math.BigDecimal.ZERO;

        // 3. Transformer chaque ligne de Panier en ligne de Commande fermée
        for (CartItem cart : cartItems) {
            Product product = cart.getProduct();
            
            // Vérification élémentaire des stocks
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour le produit : " + product.getName());
            }

            // Déduction du stock physique
            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);

            // Création de l'archive d'achat
            OrderItem orderItem = new OrderItem(order, product, cart.getQuantity());
            order.getItems().add(orderItem);

            // Cumul du prix total (BigDecimal safe arithmetic)
            globalTotal = globalTotal.add(product.getPrice().multiply(java.math.BigDecimal.valueOf(cart.getQuantity())));
        }

        order.setTotalPrice(globalTotal);

        // 4. Sauvegarder la commande en cascade (sauvegarde l'ordre + les items associés)
        Order savedOrder = orderRepository.save(order);

        // 5. Vider le panier de l'utilisateur puisqu'il a acheté
        cartItemRepository.deleteByUser(user);

        return savedOrder;
    }

    public List<Order> getOrdersByStore(Long storeId) {
        return orderRepository.findByStoreId(storeId);
    }
}