package com.example.ecommerce.service.Impl;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Rating;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.repository.RatingRepository;
import com.example.ecommerce.request.RatingRequest;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductService productService;

    @Override
    public Rating createRating(RatingRequest req, User user) throws ProductException {
        Product product = productService.findProductById(req.getProductId());

        Rating rating = new Rating();
        rating.setProduct(product);
        rating.setUser(user);
        rating.setRating(req.getRating());
        rating.setCreatedAt(LocalDateTime.now());

        return  ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getProductRating(Long productID) {
        return ratingRepository.getAllProductsRating(productID);
    }
}
