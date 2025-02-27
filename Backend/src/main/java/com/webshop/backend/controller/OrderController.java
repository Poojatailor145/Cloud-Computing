package com.webshop.backend.controller;

import com.webshop.backend.dto.OrderResponse;
import com.webshop.backend.model.Order;
import com.webshop.backend.model.OrderItem;
import com.webshop.backend.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponse> createOrder(@PathVariable Long userId, @RequestBody List<OrderItem> orderItems) {
        return ResponseEntity.ok(orderService.createOrder(userId, orderItems));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/order/{orderId}/status")
    public ResponseEntity<Map<String, String>> getUserOrderStatus(@PathVariable Long userId, @PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (!order.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No order for this user found"));
            }
            return ResponseEntity.ok(Collections.singletonMap("status", order.getStatus()));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

}
