package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true) // true au cas où le produit physique est supprimé plus tard
    private Product product;

    private String productNameSnapshot; // Sauvegarde du nom
    private java.math.BigDecimal priceSnapshot;        // Sauvegarde du prix exact à l'achat
    private int quantity;

    // Constructeurs
    public OrderItem() {}

    public OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.productNameSnapshot = product.getName();
        this.priceSnapshot = product.getPrice();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getProductNameSnapshot() { return productNameSnapshot; }
    public void setProductNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; }
    public java.math.BigDecimal getPriceSnapshot() { return priceSnapshot; }
    public void setPriceSnapshot(java.math.BigDecimal priceSnapshot) { this.priceSnapshot = priceSnapshot; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}