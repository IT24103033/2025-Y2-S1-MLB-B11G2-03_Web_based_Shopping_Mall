package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.strategy.PaymentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

//PaymentTestController - Demonstrates Strategy Pattern Implementation
@RestController
@RequestMapping("/api/payment-test")
public class PaymentTestController {
    
    @Autowired
    private PaymentContext paymentContext;
    
    //Test payment processing with different strategies
    @GetMapping("/{paymentMethod}")
    @ResponseBody
    public String testPayment(@PathVariable String paymentMethod, 
                             @RequestParam(defaultValue = "100.00") BigDecimal amount) {
        
        String testOrderId = "TEST_ORDER_" + System.currentTimeMillis();
        
        System.out.println("\nSTRATEGY PATTERN DEMO - Payment Test");
        System.out.println("=====================================");
        System.out.println("Testing payment method: " + paymentMethod);
        System.out.println("Test amount: $" + amount);
        System.out.println("Test order ID: " + testOrderId);
        
        // Use PaymentContext to process payment (Strategy Pattern in action!)
        PaymentContext.PaymentResult result = paymentContext.processPayment(
            paymentMethod, 
            amount, 
            testOrderId
        );
        
        // Format result for web response
        StringBuilder response = new StringBuilder();
        response.append("STRATEGY PATTERN TEST RESULTS\n");
        response.append("================================\n");
        response.append("Payment Method: ").append(paymentMethod).append("\n");
        response.append("Test Amount: $").append(amount).append("\n");
        response.append("Order ID: ").append(testOrderId).append("\n");
        response.append("Status: ").append(result.isSuccessful() ? "SUCCESS" : "FAILED").append("\n");
        response.append("Message: ").append(result.getMessage()).append("\n");
        
        if (result.isSuccessful()) {
            response.append("Strategy Used: ").append(result.getPaymentMethod()).append("\n");
        }
        
        response.append("\nStrategy Pattern Benefits Demonstrated:");
        response.append("\n- Same method call works for different payment types");
        response.append("\n- Easy to add new payment methods");
        response.append("\n- No if/else chains in client code");
        response.append("\n- Each strategy is independent and testable");
        
        return response.toString();
    }
    
    //Get information about available payment methods
    @GetMapping("/methods")
    @ResponseBody
    public String getPaymentMethods() {
        StringBuilder response = new StringBuilder();
        response.append("AVAILABLE PAYMENT METHODS (Strategy Pattern)\n");
        response.append("=============================================\n\n");
        
        // Test each available payment method
        String[] methods = {"cash", "card"};
        
        for (String method : methods) {
            PaymentContext.PaymentMethodInfo info = paymentContext.getPaymentMethodInfo(method);
            if (info != null) {
                response.append("💳 ").append(method.toUpperCase()).append(" PAYMENT\n");
                response.append("   Display Name: ").append(info.getName()).append("\n");
                response.append("   Requires Card Details: ").append(info.requiresCardDetails() ? "Yes" : "No").append("\n");
                response.append("   Test URL: /api/payment-test/").append(method).append("?amount=100\n\n");
            }
        }
        
        response.append("Try these test URLs:\n");
        response.append("- /api/payment-test/cash?amount=50\n");
        response.append("- /api/payment-test/card?amount=75.99\n");
        response.append("- /api/payment-test/invalid?amount=100  (to see error handling)\n");
        
        return response.toString();
    }
}