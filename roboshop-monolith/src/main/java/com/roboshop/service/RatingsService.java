package com.roboshop.service;

import com.roboshop.model.Product;
import com.roboshop.model.Rating;
import com.roboshop.model.User;
import com.roboshop.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingsService {

    private final RatingRepository ratingRepository;

    public RatingsService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating addRating(Product product, User user, int score, String review) {
        Optional<Rating> existing = ratingRepository.findByUserIdAndProductId(user.getId(), product.getId());
        Rating rating;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.setScore(score);
            rating.setReview(review);
        } else {
            rating = new Rating();
            rating.setProduct(product);
            rating.setUser(user);
            rating.setScore(score);
            rating.setReview(review);
        }
        return ratingRepository.save(rating);
    }

    public List<Rating> getProductRatings(Long productId) {
        return ratingRepository.findByProductId(productId);
    }

    public Double getAverageRating(Long productId) {
        return ratingRepository.findAverageScoreByProductId(productId).orElse(0.0);
    }

    public Long getRatingCount(Long productId) {
        return ratingRepository.countByProductId(productId);
    }
}
