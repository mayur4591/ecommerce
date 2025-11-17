package com.example.ecommerce.service;

import com.example.ecommerce.entity.Address;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.OrderException;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, Address shipppingAddress);
    Order findOrderById(Long orderId) throws OrderException;
    List<Order> usersOrderHistory(Long userId);
    Order placedOrder(Long orderId) throws OrderException;
    Order confirmedOrder(Long orderId) throws OrderException;
    Order shippedOrder(Long orderId) throws OrderException;
    Order deliveredOrder(Long orderId) throws OrderException;
    Order cancledOrder(Long orderId) throws OrderException;
    void deleteOrder(Long orderId) throws OrderException;
    List<Order> getAllOrders();
}
