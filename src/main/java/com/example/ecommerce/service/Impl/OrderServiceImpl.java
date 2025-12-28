package com.example.ecommerce.service.Impl;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.OrderException;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public Order createOrder(User user, Address shipppingAddress) {
        shipppingAddress.setUser(user);
        Address address = addressRepository.save(shipppingAddress);
        user.getAddresses().add(address);
        userRepository.save(user);

        Cart cart =cartService.findUserCart(user.getId());
        List<OrderItem> orderItems=new ArrayList<>();

        for(CartItem item: cart.getCartItems()){
            OrderItem orderItem = new OrderItem();

            orderItem.setPrice(item.getPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSize(item.getSize());
            orderItem.setUserId(item.getUserId());
            orderItem.setDiscountedPrice(item.getDiscountedPrice());

            OrderItem createdOrderItem = orderItemRepository.save(orderItem);

            orderItems.add(orderItem);
        }

        Order createdOrder = new Order();
        createdOrder.setUser(user);
        createdOrder.setOrderItems(orderItems);
        createdOrder.setTotalPrice(cart.getTotalPrice());
        createdOrder.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
        createdOrder.setDiscount(cart.getDiscount());
        createdOrder.setTotalItem(cart.getTotalItem());

        createdOrder.setShippingAddress(address);
        createdOrder.setOrderDate(LocalDateTime.now());
        createdOrder.setOrderStatus("PENDING");
        createdOrder.getPaymentDetails().setStatus("PENDING");
        createdOrder.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(createdOrder);

        for(OrderItem item : orderItems){
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderException {

        log.debug("Fetching order by orderId={}", orderId);

        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isPresent()) {
            log.debug("Order found with orderId={}", orderId);
            return order.get();
        }

        log.warn("Order not found with orderId={}", orderId);
        throw new OrderException("Order not found with id " + orderId);
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {

        log.info("Fetching order history for userId={}", userId);

        List<Order> orders = orderRepository.getUsersOrders(userId);

        log.info("Found {} orders for userId={}", orders.size(), userId);
        return orders;
    }

    @Override
    public Order placedOrder(Long orderId) throws OrderException {

        log.info("Placing order with orderId={}", orderId);

        Order order = findOrderById(orderId);
        order.setOrderStatus("PLACED");
        order.getPaymentDetails().setStatus("COMPLETED");

        Order updatedOrder = orderRepository.save(order);

        log.info("Order placed successfully with orderId={}", orderId);
        return updatedOrder;
    }

    @Override
    public Order confirmedOrder(Long orderId) throws OrderException {

        log.info("Confirming order with orderId={}", orderId);

        Order order = findOrderById(orderId);
        order.setOrderStatus("CONFIRMED");

        Order updatedOrder = orderRepository.save(order);

        log.info("Order confirmed with orderId={}", orderId);
        return updatedOrder;
    }

    @Override
    public Order shippedOrder(Long orderId) throws OrderException {

        log.info("Shipping order with orderId={}", orderId);

        Order order = findOrderById(orderId);
        order.setOrderStatus("SHIPPED");

        Order updatedOrder = orderRepository.save(order);

        log.info("Order shipped with orderId={}", orderId);
        return updatedOrder;
    }

    @Override
    public Order deliveredOrder(Long orderId) throws OrderException {

        log.info("Delivering order with orderId={}", orderId);

        Order order = findOrderById(orderId);
        order.setOrderStatus("DELIVERED");

        Order updatedOrder = orderRepository.save(order);

        log.info("Order delivered with orderId={}", orderId);
        return updatedOrder;
    }

    @Override
    public Order cancledOrder(Long orderId) throws OrderException {

        log.info("Cancelling order with orderId={}", orderId);

        Order order = findOrderById(orderId);
        order.setOrderStatus("CANCELLED");

        Order updatedOrder = orderRepository.save(order);

        log.info("Order cancelled with orderId={}", orderId);
        return updatedOrder;
    }

    @Override
    public void deleteOrder(Long orderId) throws OrderException {

        log.warn("Deleting order with orderId={}", orderId);

        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);

        log.warn("Order deleted with orderId={}", orderId);
    }

    @Override
    public List<Order> getAllOrders() {

        log.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();

        log.info("Total orders found={}", orders.size());
        return orders;
    }
}
