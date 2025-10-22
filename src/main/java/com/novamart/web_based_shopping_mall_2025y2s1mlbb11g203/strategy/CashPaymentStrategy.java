package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

//CashPaymentStrategy - Concrete Strategy for Cash on Delivery
@Component("cashPayment")
public class CashPaymentStrategy implements PaymentStrategy {
    
    /**
     * Process cash payment - no actual payment processing needed
     * Just validates and logs the payment intent
     */
    @Override
    public boolean processPayment(BigDecimal amount, String orderId) {
        System.out.println("Processing Cash Payment:");
        System.out.println("   Order ID: " + orderId);
        System.out.println("   Amount: $" + amount);
        System.out.println("   Method: Cash on Delivery");
        
        // Validate amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid amount for cash payment: $" + amount);
            return false;
        }
        
        // Cash payment is always "successful" since no processing needed
        // Payment will be collected on delivery
        System.out.println("Cash payment scheduled for delivery");
        System.out.println("   Customer will pay $" + amount + " to delivery person");
        
        return true;
    }
    
    //Returns the display name for cash payment method
    @Override
    public String getPaymentMethodName() {
        return "Cash on Delivery";
    }
    
    //Cash payment doesn't require card details
    @Override
    public boolean requiresCardDetails() {
        return false;
    }
}