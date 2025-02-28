package com.webshop.backend.service;

import com.webshop.backend.dto.PaymentRequest;
import com.webshop.backend.dto.PaymentResponse;
import com.webshop.backend.model.*;
import com.webshop.backend.repository.OrderRepository;
import com.webshop.backend.repository.PaymentRepository;
import com.webshop.backend.repository.ProductRepository;
import com.webshop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    @Autowired
    private InventoryManagement inventoryManagement;
    @Autowired
    private JavaMailSender mailSender;

    public PaymentResponse processPayment(PaymentRequest request) {
        System.out.println("TEST");
        Optional<Order> optionalOrder = orderRepository.findById(request.getOrderId());
        if (optionalOrder.isEmpty()) {
            PaymentResponse response = new PaymentResponse();
            response.setStatus("FAILED");
            response.setErrorMessage("No order for this user found");
            return response;
        }
        Order order = optionalOrder.get();

        if (order.getUser() == null || !order.getUser().getUserId().equals(request.getUserId())) {
            PaymentResponse response = new PaymentResponse();
            response.setStatus("FAILED");
            response.setErrorMessage("No order for this user found");
            return response;
        }

        if (order.getStatus() != null && order.getStatus().equalsIgnoreCase("Confirmed")) {
            PaymentResponse response = new PaymentResponse();
            response.setOrderId(order.getOrderId());
            response.setUserId(order.getUser().getUserId());
            response.setStatus("SUCCESS");
            response.setErrorMessage("Payment already processed");
            return response;
        }

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
            System.out.println("test else");
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
            System.out.println("Test 1");
            for (OrderItem item : order.getOrderItems()) {
                System.out.println("Test 2");
                Product product = item.getProduct();
                if (product != null) {
                    System.out.println("Test3");
                    int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    int newStock = currentStock - item.getQuantity();
                    product.setStockQuantity(newStock);
                    productRepository.save(product);
                    System.out.println("Before check");
                    inventoryManagement.checkStockAndNotify(product);
                }
            }
            sendOrderConfirmationEmail(order);
        }

        return mapToPaymentResponse(payment);
    }

    private void sendOrderConfirmationEmail(Order order) {
        System.out.println("Email start");
        String userEmail = order.getUser().getEmail();
        String subject = "Order Confirmation - " + order.getOrderId();
        String message = "Dear " + order.getUser().getFullName() + ",\n\n" +
                "Thank you for your purchase! Your order has been confirmed.\n" +
                "Order ID: " + order.getOrderId() + "\n" +
                "Total Amount: " + order.getTotalAmount() + "\n\n" +
                "We will notify you once your order is shipped.";
        System.out.println("Email code: "+ message);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
        System.out.println("USer email"+userEmail);
        System.out.println("Email end");
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
