package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Random;

// CardPaymentStrategy - Concrete Strategy for Credit/Debit Card Payments
@Component("cardPayment")
public class CardPaymentStrategy implements PaymentStrategy {
    
    private Random random = new Random(); // For simulating payment success/failure
    
    // Process card payment - simulates real payment gateway processing
    @Override
    public boolean processPayment(BigDecimal amount, String orderId) {
        System.out.println("Processing Card Payment:");
        System.out.println("   Order ID: " + orderId);
        System.out.println("   Amount: $" + amount);
        System.out.println("   Method: Credit/Debit Card");
        
        // Validate amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid amount for card payment: $" + amount);
            return false;
        }
        
        // Simulate payment processing time
        try {
            System.out.println("Connecting to payment gateway...");
            Thread.sleep(1000); // Simulate 1-second processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate payment success/failure (90% success rate for demo)
        boolean paymentSuccess = random.nextInt(100) < 90;
        
        if (paymentSuccess) {
            // Generate fake transaction ID for successful payments
            String transactionId = "TXN_" + System.currentTimeMillis();
            System.out.println("Card payment successful!");
            System.out.println("   Transaction ID: " + transactionId);
            System.out.println("   Amount charged: $" + amount);
            return true;
        } else {
            System.out.println("Card payment failed!");
            System.out.println("   Reason: Insufficient funds or card declined");
            return false;
        }
    }
    
    //Returns the display name for card payment method
    @Override
    public String getPaymentMethodName() {
        return "Credit/Debit Card";
    }
    
    //Card payment requires card details (number, expiry, CVV)
    @Override
    public boolean requiresCardDetails() {
        return true;
    }
}