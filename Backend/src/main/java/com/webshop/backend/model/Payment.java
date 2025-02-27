package com.webshop.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    // Default constructor
    public Payment() {
    }

    // Getters and setters
    public Long getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    // Manual builder implementation
    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private final Payment payment;

        public PaymentBuilder() {
            this.payment = new Payment();
        }

        public PaymentBuilder order(Order order) {
            payment.setOrder(order);
            return this;
        }

        public PaymentBuilder user(User user) {
            payment.setUser(user);
            return this;
        }

        public PaymentBuilder paymentMethod(String paymentMethod) {
            payment.setPaymentMethod(paymentMethod);
            return this;
        }

        public PaymentBuilder paymentDate(LocalDateTime paymentDate) {
            payment.setPaymentDate(paymentDate);
            return this;
        }

        public PaymentBuilder amount(BigDecimal amount) {
            payment.setAmount(amount);
            return this;
        }

        public PaymentBuilder errorMessage(String errorMessage) {
            payment.setErrorMessage(errorMessage);
            return this;
        }

        public PaymentBuilder status(String status) {
            payment.setStatus(status);
            return this;
        }

        public Payment build() {
            return payment;
        }
    }
}
