package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.CartItemException;
import com.example.ecommerce.exception.UserException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemService {
    CartItem createCartItem(CartItem cartItem);
    CartItem updateCartItem(Long userId,Long id,CartItem cartItem) throws CartItemException, UserException;
    CartItem isCartItemExist(Cart cart, Product product, String size, Long userId);
    void removeCartItem(Long userId,Long cartItemId) throws CartItemException,UserException;
    CartItem findCartItemById(Long cartItemID) throws CartItemException;
}
