package com.webshop.backend.service;

import com.webshop.backend.dto.PaymentRequest;
import com.webshop.backend.dto.PaymentResponse;
import com.webshop.backend.model.Order;
import com.webshop.backend.model.OrderItem;
import com.webshop.backend.model.Payment;
import com.webshop.backend.model.Product;
import com.webshop.backend.model.User;
import com.webshop.backend.repository.OrderRepository;
import com.webshop.backend.repository.PaymentRepository;
import com.webshop.backend.repository.ProductRepository;
import com.webshop.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;



    public PaymentResponse processPayment(PaymentRequest request) {
        // Fetch order and user
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + request.getOrderId()));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        // Validate payment method (case-insensitive)
        boolean validPaymentMethod = request.getPaymentMethod() != null &&
                (request.getPaymentMethod().equalsIgnoreCase("paypal") ||
                        request.getPaymentMethod().equalsIgnoreCase("creditcard") ||
                        request.getPaymentMethod().equalsIgnoreCase("debitcard"));

        Payment payment;
        if (!validPaymentMethod) {
            payment = Payment.builder()
                    .order(order)
                    .user(user)
                    .paymentMethod(request.getPaymentMethod())
                    .paymentDate(LocalDateTime.now())
                    .amount(request.getAmount())
                    .status("FAILED")
                    .errorMessage("Invalid payment method provided")
                    .build();
            paymentRepository.save(payment);
            order.setStatus("Payment Failed");
            orderRepository.save(order);
        } else {
            payment = Payment.builder()
                    .order(order)
                    .user(user)
                    .paymentMethod(request.getPaymentMethod())
                    .paymentDate(LocalDateTime.now())
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .errorMessage(null)
                    .build();
            paymentRepository.save(payment);
            order.setStatus("Confirmed");
            orderRepository.save(order);

            // Decrease stock for each product in the order
            for (OrderItem item : order.getOrderItems()) {
                Product productProxy = item.getProduct();
                if (productProxy != null && productProxy.getProductId() != null) {
                    // Reload the full product entity
                    Product product = productRepository.findById(productProxy.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productProxy.getProductId()));
                    int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    int newStock = currentStock - item.getQuantity();
                    // Log for debugging (optional)
                    System.out.println("Updating product " + product.getProductId() + ": currentStock=" + currentStock +
                            ", quantity=" + item.getQuantity() + ", newStock=" + newStock);
                    product.setStockQuantity(newStock);
                    productRepository.save(product);
                }
            }


        }

        return mapToPaymentResponse(payment);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getPaymentId());
        response.setOrderId(payment.getOrder().getOrderId());
        response.setUserId(payment.getUser().getUserId());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentDate(payment.getPaymentDate());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setErrorMessage(payment.getErrorMessage());
        return response;
    }
}
