package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Address;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.OrderException;
import com.example.ecommerce.exception.UserException;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@RequestBody Address shippingAddress,
                                             @RequestHeader("Authorization") String jwt)
            throws UserException {

        log.info("Create order request received");

        User user = userService.findUserProfileByJwt(jwt);
        log.info("User authenticated successfully with userId={}", user.getId());

        Order order = orderService.createOrder(user, shippingAddress);
        log.info("Order created successfully with orderId={} for userId={}",
                order.getId(), user.getId());

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> usersOrderHistory(
            @RequestHeader("Authorization") String jwt) throws UserException {

        log.info("Fetch order history request received");

        User user = userService.findUserProfileByJwt(jwt);
        log.info("Fetching order history for userId={}", user.getId());

        List<Order> orders = orderService.usersOrderHistory(user.getId());
        log.info("Fetched {} orders for userId={}", orders.size(), user.getId());

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findOrderById(
            @PathVariable("id") Long orderId) throws OrderException {

        log.info("Fetch order request received for orderId={}", orderId);

        Order order = orderService.findOrderById(orderId);
        log.info("Order fetched successfully for orderId={}", orderId);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
