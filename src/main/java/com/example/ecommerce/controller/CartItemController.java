package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.CartItemException;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.response.ApiResponse;
import com.example.ecommerce.service.CartItemService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/cart_items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private UserService userService;

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable Long cartItemId,
                                                      @RequestHeader("Authorization") String jwt) throws UserException, CartItemException{
        User user =userService.findUserProfileByJwt(jwt);
        cartItemService.removeCartItem(user.getId(),cartItemId);
        ApiResponse response = new ApiResponse();
        response.setMessage("Delete item from cart");
        response.setStatus(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
