package com.example.ecommerce.service;

import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.request.ReviewRequest;

import java.util.List;

public interface ReviewService {

    Review createReview(ReviewRequest req, User user)throws ProductException;
    List<Review> getAllReview(Long productId);
}
