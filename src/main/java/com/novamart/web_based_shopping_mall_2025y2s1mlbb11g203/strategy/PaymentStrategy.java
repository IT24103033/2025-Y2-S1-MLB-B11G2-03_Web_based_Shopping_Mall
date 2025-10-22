package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.strategy;

import java.math.BigDecimal;

//PaymentStrategy Interface
public interface PaymentStrategy {
    
    //Process payment for the given amount and order
    boolean processPayment(BigDecimal amount, String orderId);
    
    //Get the name of this payment method for display purposes
    String getPaymentMethodName();
    
    //Check if this payment method requires card details
    boolean requiresCardDetails();
}