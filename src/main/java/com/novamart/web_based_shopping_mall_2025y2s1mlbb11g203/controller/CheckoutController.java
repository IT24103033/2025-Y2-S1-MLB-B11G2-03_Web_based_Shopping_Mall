package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

/**
 * CheckoutController - handles checkout web requests
 * Provides endpoints for the checkout process
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    
    @Autowired
    private CheckoutService checkoutService;
    
    @Autowired
    private com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.OrderItemRepository orderItemRepository;
    
    /**
     * Get checkout summary - shows cart items and total
     * GET /api/checkout/summary
     */
    @GetMapping("/summary")
    public Object getCheckoutSummary(HttpSession session) {
        try {
            // Check if user is authenticated (placeholder for future)
            if (!checkoutService.isUserAuthenticated(session)) {
                return "Authentication required. Please log in to checkout.";
                // TODO: When login is implemented, redirect to login page
            }
            
            CheckoutService.CheckoutSummary summary = checkoutService.getCheckoutSummary(session);
            
            if (summary == null) {
                return "Your cart is empty. Add items before checkout.";
            }
            
            return summary;
            
        } catch (Exception e) {
            return "Failed to get checkout summary: " + e.getMessage();
        }
    }
    
    /**
     * Process checkout with payment method
     * POST /api/checkout/process?paymentMethod=cash
     * POST /api/checkout/process?paymentMethod=card
     */
    @PostMapping("/process")
    public String processCheckout(@RequestParam String paymentMethod, 
                                 HttpSession session) {
        try {
            // Check if user is authenticated (placeholder for future)
            if (!checkoutService.isUserAuthenticated(session)) {
                return "Authentication required. Please log in to checkout.";
                // TODO: When login is implemented, redirect to login page
            }
            
            // Validate payment method
            if (!paymentMethod.equals("cash") && !paymentMethod.equals("card")) {
                return "Invalid payment method. Use 'cash' or 'card'.";
            }
            
            // Process the checkout
            String orderId = checkoutService.processCheckout(session, paymentMethod);
            
            if (orderId != null) {
                return "Checkout successful! Order ID: " + orderId + 
                       ". Payment method: " + paymentMethod + 
                       ". You will receive an email confirmation.";
            } else {
                return "Checkout failed. Please try again or contact support.";
            }
            
        } catch (Exception e) {
            return "Checkout failed: " + e.getMessage();
        }
    }

    /**
     * DEBUG endpoint: process checkout and return created order items (for verification)
     * POST /api/checkout/process-return?paymentMethod=card
     */
    @PostMapping("/process-return")
    public Object processCheckoutAndReturn(@RequestParam String paymentMethod, HttpSession session) {
        try {
            String orderId = checkoutService.processCheckout(session, paymentMethod);
            if (orderId == null) {
                return "Checkout failed";
            }
            // Fetch order items created for this order
            java.util.List<com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.OrderItem> items = orderItemRepository.findByOrderId(orderId);
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("orderId", orderId);
            result.put("itemsCount", items == null ? 0 : items.size());
            result.put("items", items);
            return result;
        } catch (Exception e) {
            return "Checkout failed: " + e.getMessage();
        }
    }
    
    // TESTING ENDPOINTS - Browser-friendly GET requests for testing
    
    /**
     * Test checkout with cash payment via GET (for browser testing)
     * GET /api/checkout/test-cash
     */
    @GetMapping("/test-cash")
    public String testCheckoutCash(HttpSession session) {
        return processCheckout("cash", session);
    }
    
    /**
     * Test checkout with card payment via GET (for browser testing)
     * GET /api/checkout/test-card
     */
    @GetMapping("/test-card")
    public String testCheckoutCard(HttpSession session) {
        return processCheckout("card", session);
    }
}