package com.webshop.backend.service;

import com.webshop.backend.dto.PaymentRequest;
import com.webshop.backend.dto.PaymentResponse;
import com.webshop.backend.model.Order;
import com.webshop.backend.model.OrderItem;
import com.webshop.backend.model.Payment;
import com.webshop.backend.model.Product;
import com.webshop.backend.repository.OrderRepository;
import com.webshop.backend.repository.PaymentRepository;
import com.webshop.backend.repository.ProductRepository;
import com.webshop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

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
        // Check if the order exists
        Optional<Order> optionalOrder = orderRepository.findById(request.getOrderId());
        if (optionalOrder.isEmpty()) {
            PaymentResponse response = new PaymentResponse();
            response.setStatus("FAILED");
            response.setErrorMessage("No order for this user found");
            return response;
        }
        Order order = optionalOrder.get();

        // Verify that the order belongs to the given user
        if (order.getUser() == null || !order.getUser().getUserId().equals(request.getUserId())) {
            PaymentResponse response = new PaymentResponse();
            response.setStatus("FAILED");
            response.setErrorMessage("No order for this user found");
            return response;
        }

        // If the order is already confirmed, return a response indicating payment is already done
        if (order.getStatus() != null && order.getStatus().equalsIgnoreCase("Confirmed")) {
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(order.getOrderId());
            response.setUserId(order.getUser().getUserId());
            response.setStatus("SUCCESS");
            response.setErrorMessage("Payment already processed");
            return response;
        }

        // Validate payment method and required fields
        boolean validPaymentMethod = false;
        String errorMessage = null;

        if (request.getPaymentMethod() != null) {
            if (request.getPaymentMethod().equalsIgnoreCase("paypal")) {
                if (request.getPaypalEmail() != null && request.getPaypalPassword() != null) {
                    validPaymentMethod = true;
                } else {
                    errorMessage = "PayPal payment requires email and password.";
                }
            } else if (request.getPaymentMethod().equalsIgnoreCase("creditcard") ||
                    request.getPaymentMethod().equalsIgnoreCase("debitcard")) {
                if (request.getCardNumber() != null &&
                        request.getCardExpiry() != null &&
                        request.getCardCvv() != null) {
                    validPaymentMethod = true;
                } else {
                    errorMessage = "Card payment requires card number, expiry, and CVV.";
                }
            } else {
                errorMessage = "Unsupported payment method.";
            }
        } else {
            errorMessage = "Payment method must be provided.";
        }

        Payment payment;
        if (!validPaymentMethod) {
            // Create a failed payment record
            payment = Payment.builder()
                    .order(order)
                    .user(order.getUser())
                    .paymentMethod(request.getPaymentMethod())
                    .paymentDate(LocalDateTime.now())
                    .amount(request.getAmount())
                    .status("FAILED")
                    .errorMessage(errorMessage)
                    .build();
            paymentRepository.save(payment);
            order.setStatus("Payment Failed");
            orderRepository.save(order);
        } else {
            // Simulate a successful payment
            payment = Payment.builder()
                    .order(order)
                    .user(order.getUser())
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
                Product product = item.getProduct();
                if (product != null) {
                    int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    int newStock = currentStock - item.getQuantity();
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