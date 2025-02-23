package com.webshop.backend.service;

import com.webshop.backend.model.*;
import com.webshop.backend.repository.OrderRepository;
import com.webshop.backend.repository.OrderItemRepository;
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


    public OrderResponse createOrder(Long userId, List<OrderItem> orderItems) {
        // Retrieve the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

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

        // Save the Order (this will cascade persist the OrderItems, if configured)
        Order savedOrder = orderRepository.save(order);

        // Convert the saved Order entity to OrderResponse DTO
        return convertToResponse(savedOrder);
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

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUser().getUserId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setOrderDate(order.getOrderDate());

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setOrderItemId(item.getOrderItemId());
            // Assuming product is not null and has a productId
            itemResponse.setProductId(item.getProduct() != null ? item.getProduct().getProductId() : null);
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            return itemResponse;
        }).collect(Collectors.toList());

        response.setOrderItems(itemResponses);
        return response;
    }
}
