package com.example.ecommerce.service.Impl;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.request.AddItemRequest;
import com.example.ecommerce.service.CartItemService;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ProductService productService;

    @Override
    public Cart createCart(User user) {

        log.info("Creating cart for userId={}", user.getId());

        Cart cart = new Cart();
        cart.setUser(user);

        Cart savedCart = cartRepository.save(cart);

        log.info("Cart created successfully with cartId={} for userId={}",
                savedCart.getId(), user.getId());

        return savedCart;
    }

    @Override
    public String addCartItem(Long userId, AddItemRequest req) throws ProductException {

        log.info("Adding item to cart for userId={}, productId={}",
                userId, req.getProductId());

        Cart cart = cartRepository.findByUserId(userId);
        log.debug("Fetched cartId={} for userId={}", cart.getId(), userId);

        Product product = productService.findProductById(req.getProductId());
        log.debug("Fetched productId={} successfully", product.getId());

        CartItem isPresent = cartItemService.isCartItemExist(
                cart, product, req.getSize(), userId
        );

        if (isPresent == null) {
            log.info("CartItem not present, creating new cart item");

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);
            cartItem.setSize(req.getSize());
            cartItem.setDiscountedPrice(product.getDiscountedPrice());

            int price = req.getQuantity() * product.getDiscountedPrice();
            cartItem.setPrice(price);
            cartItem.setSize(req.getSize());

            CartItem createdCartItem = cartItemService.createCartItem(cartItem);

            log.info("CartItem created successfully with cartItemId={} for userId={}",
                    createdCartItem.getId(), userId);
        } else {
            log.info("CartItem already exists for userId={}, productId={}",
                    userId, product.getId());
        }

        return "Item Add to cart";
    }

    @Override
    @Transactional
    public Cart findUserCart(Long userId) {

        log.info("Fetching cart for userId={}", userId);

        Cart cart = cartRepository.findByUserId(userId);

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            totalPrice = totalPrice + cartItem.getPrice();
            totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
            totalItem = totalItem + cartItem.getQuantity();
        }

        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(totalPrice - totalDiscountedPrice);

        log.info("Cart calculated for userId={} | totalItems={} | totalPrice={} | discount={}",
                userId, totalItem, totalPrice, cart.getDiscount());

        Cart updatedCart = cartRepository.save(cart);

        log.info("Cart updated successfully for userId={}", userId);

        return updatedCart;
    }
}
