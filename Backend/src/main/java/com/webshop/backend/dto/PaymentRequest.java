package com.webshop.backend.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private Long orderId;
    private Long userId;
    private String paymentMethod; // Allowed: "paypal", "creditcard", "debitcard"

    // Fields for card payments:
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;

    // Fields for PayPal:
    private String paypalEmail;
    private String paypalPassword;

    private BigDecimal amount;

    // Getters and setters
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getCardExpiry() {
        return cardExpiry;
    }
    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }
    public String getCardCvv() {
        return cardCvv;
    }
    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }
    public String getPaypalEmail() {
        return paypalEmail;
    }
    public void setPaypalEmail(String paypalEmail) {
        this.paypalEmail = paypalEmail;
    }
    public String getPaypalPassword() {
        return paypalPassword;
    }
    public void setPaypalPassword(String paypalPassword) {
        this.paypalPassword = paypalPassword;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
