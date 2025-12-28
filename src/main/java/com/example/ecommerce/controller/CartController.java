package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.request.AddItemRequest;
import com.example.ecommerce.response.ApiResponse;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<Cart> findUserCart(
            @RequestHeader("Authorization") String jwt) throws UserException {

        log.info("Fetch cart request received");

        User user = userService.findUserProfileByJwt(jwt);
        log.info("User authenticated successfully with userId={}", user.getId());

        Cart cart = cartService.findUserCart(user.getId());
        log.info("Cart fetched successfully for userId={}", user.getId());

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addItemToCart(
            @RequestBody AddItemRequest request,
            @RequestHeader("Authorization") String jwt)
            throws UserException, ProductException {

        log.info("Add item to cart request received");

        User user = userService.findUserProfileByJwt(jwt);
        log.info("User authenticated successfully with userId={}", user.getId());

        cartService.addCartItem(user.getId(), request);
        log.info("Item added to cart successfully for userId={}", user.getId());

        ApiResponse response = new ApiResponse();
        response.setMessage("Item added to cart");
        response.setStatus(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
