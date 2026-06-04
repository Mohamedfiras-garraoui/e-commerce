package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    // Ajouter un avis sur un produit
    public Review addReview(Long productId, Review review, User user) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5.");
        }

        review.setProduct(product);
        review.setUser(user);
        
        return reviewRepository.save(review);
    }

    // Calculer la note moyenne d'un produit
    public double getAverageRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            return 0.0; // Aucune note pour le moment
        }

        double sum = 0;
        for (Review r : reviews) {
            sum += r.getRating();
        }

        // Renvoie la moyenne arrondie à une décimale (ex: 4.5)
        return Math.round((sum / reviews.size()) * 10.0) / 10.0;
    }

    // Récupérer la liste des avis d'un produit
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}