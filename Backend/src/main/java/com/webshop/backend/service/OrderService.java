package com.webshop.backend.service;

import com.webshop.backend.dto.OrderItemResponse;
import com.webshop.backend.dto.OrderResponse;
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


    public OrderResponse createOrder(Long userId, List<OrderItem> orderItems) {
        // Retrieve the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Validate stock for each order item and reload the full product entity
        for (OrderItem item : orderItems) {
            if (item.getProduct() == null || item.getProduct().getProductId() == null) {
                throw new IllegalArgumentException("Product must be provided for each order item");
            }
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + item.getProduct().getProductId()));

            // Check if product is out of stock
            if (product.getStockQuantity() == 0) {
                throw new IllegalArgumentException("Product " + product.getProductName() + " is Out Of Stock");
            }

            // Check if requested quantity exceeds available stock
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName());
            }

            // Set the fully loaded product to the order item
            item.setProduct(product);
        }

        // Calculate the total order amount
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build the Order (using your manual builder, for example)
        Order order = Order.builder()
                .user(user)
                .status("Pending")
                .totalAmount(totalAmount)
                .shippingAddress(user.getAddress())  // or use a provided shipping address from the request
                .orderDate(LocalDateTime.now())
                .build();

        // Associate each OrderItem with the Order
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        // Save the order (cascade should persist the orderItems)
        Order savedOrder = orderRepository.save(order);

        // Convert the saved order into a response DTO to return clean JSON.
        return convertToResponse(savedOrder);
    }


    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUser().getUserId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingAddress(order.getShippingAddress());
        response.setOrderDate(order.getOrderDate());

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setOrderItemId(item.getOrderItemId());
                    // Ensure product is not null and has an ID
                    if(item.getProduct() != null) {
                        itemResponse.setProductId(item.getProduct().getProductId());
                    }
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setUnitPrice(item.getUnitPrice());
                    return itemResponse;
                })
                .collect(Collectors.toList());
        response.setOrderItems(itemResponses);
        return response;
    }


    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserUserId(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("No order found with id: " + orderId));
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
