package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

//PaymentContext - Context Class for Strategy Pattern
@Component
public class PaymentContext {
    
    // Inject concrete payment strategies using Spring's dependency injection
    @Autowired
    @Qualifier("cashPayment")
    private PaymentStrategy cashPaymentStrategy;
    
    @Autowired
    @Qualifier("cardPayment")
    private PaymentStrategy cardPaymentStrategy;
    
    //Process payment using the appropriate strategy based on payment method
    public PaymentResult processPayment(String paymentMethod, BigDecimal amount, String orderId) {
        System.out.println("\nPaymentContext: Processing payment...");
        System.out.println("   Payment Method: " + paymentMethod);
        
        // Select appropriate strategy based on payment method
        PaymentStrategy strategy = getPaymentStrategy(paymentMethod);
        
        if (strategy == null) {
            System.out.println("Unsupported payment method: " + paymentMethod);
            return new PaymentResult(false, "Unsupported payment method: " + paymentMethod, null);
        }
        
        System.out.println("Selected Strategy: " + strategy.getPaymentMethodName());
        
        // Delegate to the selected strategy
        boolean success = strategy.processPayment(amount, orderId);
        
        // Create and return result
        PaymentResult result = new PaymentResult(
            success,
            success ? "Payment processed successfully via " + strategy.getPaymentMethodName()
                   : "Payment failed via " + strategy.getPaymentMethodName(),
            strategy.getPaymentMethodName()
        );
        
        System.out.println("Payment processing completed: " + (success ? "SUCCESS" : "FAILED"));
        
        return result;
    }
    
    //Get the appropriate payment strategy based on payment method string
    private PaymentStrategy getPaymentStrategy(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        
        return switch (paymentMethod.toLowerCase()) {
            case "cash" -> cashPaymentStrategy;
            case "card" -> cardPaymentStrategy;
            default -> null; // Unsupported payment method
        };
    }
    
    //Get payment method details without processing
    public PaymentMethodInfo getPaymentMethodInfo(String paymentMethod) {
        PaymentStrategy strategy = getPaymentStrategy(paymentMethod);
        
        if (strategy == null) {
            return null;
        }
        
        return new PaymentMethodInfo(
            strategy.getPaymentMethodName(),
            strategy.requiresCardDetails()
        );
    }
    
    //PaymentResult - Contains the result of payment processing
    public static class PaymentResult {
        private final boolean successful;
        private final String message;
        private final String paymentMethod;
        
        public PaymentResult(boolean successful, String message, String paymentMethod) {
            this.successful = successful;
            this.message = message;
            this.paymentMethod = paymentMethod;
        }
        
        // Getters
        public boolean isSuccessful() { return successful; }
        public String getMessage() { return message; }
        public String getPaymentMethod() { return paymentMethod; }
    }
    
    //PaymentMethodInfo - Contains information about a payment method
    public static class PaymentMethodInfo {
        private final String name;
        private final boolean requiresCardDetails;
        
        public PaymentMethodInfo(String name, boolean requiresCardDetails) {
            this.name = name;
            this.requiresCardDetails = requiresCardDetails;
        }
        
        // Getters
        public String getName() { return name; }
        public boolean requiresCardDetails() { return requiresCardDetails; }
    }
}