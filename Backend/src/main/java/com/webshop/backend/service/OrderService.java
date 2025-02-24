package com.webshop.backend.service;

import com.webshop.backend.model.*;
import com.webshop.backend.repository.OrderRepository;
import com.webshop.backend.repository.OrderItemRepository;
import com.webshop.backend.repository.ProductRepository;
import com.webshop.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    public Order createOrder(Long userId, List<OrderItem> orderItems) {
        // Retrieve the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        orderItems.forEach(item -> {
            if (item.getProduct() != null && item.getProduct().getProductId() != null) {
                Product fullProduct = productRepository.findById(item.getProduct().getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + item.getProduct().getProductId()));
                item.setProduct(fullProduct);
            } else {
                throw new IllegalArgumentException("Product must be provided for each order item");
            }
        });

        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build the Order using your manual builder
        Order order = Order.builder()
                .user(user)
                .status("Pending")
                .totalAmount(totalAmount)
                // For shippingAddress, you can customize (here using user's address as example)
                .shippingAddress(user.getAddress())
                .orderDate(LocalDateTime.now())
                .build();

        // Associate each OrderItem with the Order
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);


        return orderRepository.save(order);

    }


    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

}
