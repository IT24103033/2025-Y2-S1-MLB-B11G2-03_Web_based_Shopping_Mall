package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.*;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.*;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.strategy.PaymentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * CheckoutService - handles the checkout process
 * Converts cart items into completed orders
 */
@Service
public class CheckoutService {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    //Inject PaymentContext for handling different payment methods
    @Autowired
    private PaymentContext paymentContext;
    
    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Check if user is authenticated
     * Uses Spring Security authentication
     */
    public boolean isUserAuthenticated(HttpSession session) {
        return cartService.isUserAuthenticated();
    }
    
    
    // Get checkout summary - shows cart items and total before checkout
    public CheckoutSummary getCheckoutSummary(HttpSession session) {
        List<CartItem> cartItems = cartService.getCartItems(session);
        
        if (cartItems.isEmpty()) {
            return null; // No items to checkout
        }
        
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                BigDecimal itemTotal = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        
        CheckoutSummary summary = new CheckoutSummary();
        summary.setItems(cartItems);
        summary.setTotalAmount(totalAmount);
        summary.setItemCount(cartItems.size());
        
        return summary;
    }
    
    //Process checkout - creates order and order items
    @Transactional  // Ensures all database operations succeed or all fail
    public String processCheckout(HttpSession session, String paymentMethod) {
        try {
            // Get authenticated user - now uses Spring Security
            if (!cartService.isUserAuthenticated()) {
                return null; // User must be logged in to checkout
            }
            
            List<CartItem> cartItems = cartService.getCartItems(session);
            
            if (cartItems.isEmpty()) {
                return null; // No items to checkout
            }
            
            // Get user ID from first cart item (all items belong to same user)
            Long userId = cartItems.get(0).getUserId();
            
            // Create new order
            Order order = new Order();
            String orderId = "ORDER_" + UUID.randomUUID().toString();
            order.setOrderId(orderId);
            order.setUserId(String.valueOf(userId)); // Convert Long to String for Order entity
            order.setStatus("processing"); // Initial status
            order.setIsEmail(true); // Default to email notifications
            
            // Calculate total amount
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                if (product != null) {
                    BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                    totalAmount = totalAmount.add(subtotal);
                }
            }
            
            // STRATEGY PATTERN: Process payment using appropriate strategy
            System.out.println("\n=== PAYMENT PROCESSING ===");
            PaymentContext.PaymentResult paymentResult = paymentContext.processPayment(
                paymentMethod, 
                totalAmount, 
                orderId
            );
            
            // Check if payment was successful
            if (!paymentResult.isSuccessful()) {
                System.out.println("Payment failed: " + paymentResult.getMessage());
                System.out.println("Order creation cancelled due to payment failure");
                return null; // Return null to indicate checkout failure
            }
            
            System.out.println("Payment successful via " + paymentResult.getPaymentMethod());
            
            // Set total amount
            order.setTotalAmount(totalAmount);
            
            // Set total amount and save order FIRST (before order items)
            orderRepository.save(order);
            
            // Create payment record after successful payment processing
            Payment payment = new Payment();
            String paymentId = "PAY_" + UUID.randomUUID().toString();
            payment.setPaymentId(paymentId);
            payment.setOrderId(orderId);
            payment.setAmount(totalAmount);
            payment.setPaymentMethod(paymentResult.getPaymentMethod());
            payment.setPaymentStatus("completed");

            // Generate transaction ID based on payment method
            payment.setTransactionId("TXN_" + paymentId.substring(4));
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCreatedDate(LocalDateTime.now());
            payment.setUpdatedDate(LocalDateTime.now());
            
            // Save payment record to database
            paymentRepository.save(payment);
            System.out.println("Payment record saved: " + payment.getPaymentId());
            
            // Now create order items (order exists in database)
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                if (product != null) {
                    // Create order item
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderItemId(UUID.randomUUID().toString());
                    orderItem.setOrderId(orderId);
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(product.getPrice());
                    
                    // Calculate subtotal
                    BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                    orderItem.setSubtotal(subtotal);
                    orderItem.setCreatedDate(LocalDateTime.now());
                    
                    // Save order item (order already exists)
                    orderItemRepository.save(orderItem);
                    
                    System.out.println("Created order item: " + product.getName() + 
                                     " x" + cartItem.getQuantity());
                }
            }
            
            // Clear cart after successful checkout
            cartService.clearCart(session);
            
            // Send notification to customer about order completion
            try {
                notificationService.getNotification(orderId);
                System.out.println("Order completion notification sent to user: " + userId);
            } catch (Exception notificationError) {
                System.out.println("Warning: Failed to send notification: " + notificationError.getMessage());
                // Don't fail the checkout if notification fails
            }
            
            System.out.println("\n=== CHECKOUT COMPLETED SUCCESSFULLY ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("Payment Method: " + paymentResult.getPaymentMethod());
            System.out.println("Total Amount: $" + totalAmount);
            System.out.println("==========================================");
            
            return orderId;
            
        } catch (Exception e) {
            System.out.println("Checkout failed: " + e.getMessage());
            return null;
        }
    }
    
    
    // Class to hold checkout summary data
    public static class CheckoutSummary {
        private List<CartItem> items;
        private BigDecimal totalAmount;
        private int itemCount;
        
        // Getters and setters
        public List<CartItem> getItems() { return items; }
        public void setItems(List<CartItem> items) { this.items = items; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public int getItemCount() { return itemCount; }
        public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    }
}