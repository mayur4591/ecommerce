package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Rating;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.request.RatingRequest;
import com.example.ecommerce.service.RatingService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @PostMapping("/create")
    public ResponseEntity<Rating> createRating(@RequestBody RatingRequest req,
                                               @RequestHeader("Authorization") String jwt) throws UserException, ProductException{
        User user = userService.findUserProfileByJwt(jwt);

        Rating rating = ratingService.createRating(req,user);

        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getProductRating(@PathVariable Long productId,
                                                         @RequestHeader("Authorization") String jwt) throws UserException,ProductException{
        User user = userService.findUserProfileByJwt(jwt);

        List<Rating> ratings = ratingService.getProductRating(productId);

        return new ResponseEntity<>(ratings,HttpStatus.OK);
    }
}
