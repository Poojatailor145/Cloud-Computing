package com.webshop.backend.model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventoryManagement {
    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(InventoryManagement.class);
    private static final String ADMIN_EMAIL = "piyushgarg2k@gmail.com";

    public void checkStockAndNotify(Product product) {
        int stock = product.getStockQuantity();

        logger.info("Checking stock for: {} - Stock: {}", product.getProductName(), stock);
        if (stock == 5 || stock == 1) {
            sendStockAlertEmail(product);
        }
    }

    private void sendStockAlertEmail(Product product) {
        String subject = "Stock Alert: " + product.getProductName();
        String message = "The stock for product '" + product.getProductName() + "' is now at " + product.getStockQuantity() + ". Please restock soon.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(ADMIN_EMAIL);
        mailMessage.setFrom("mailtrap@example.com");
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        System.out.println("Sending email: " + subject);
        mailSender.send(mailMessage);
    }

}
