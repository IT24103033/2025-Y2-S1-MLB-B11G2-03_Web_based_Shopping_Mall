package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.*;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.*;
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

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Check if user is authenticated (placeholder for future implementation)
     * For now, temporary users can checkout
     */
    public boolean isUserAuthenticated(HttpSession session) {
        String userId = cartService.getOrCreateUserId(session);
        // TODO: When user login is implemented, check if user is logged in
        // For now, allow temporary users to checkout
        return userId != null;
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
    
    /**
     * Process checkout - creates order and order items
     * @param session - user session
     * @param paymentMethod - "cash" or "card"
     * @return order ID if successful, null if failed
     */
    @Transactional  // Ensures all database operations succeed or all fail
    public String processCheckout(HttpSession session, String paymentMethod) {
        try {
            String userId = cartService.getOrCreateUserId(session);
            List<CartItem> cartItems = cartService.getCartItems(session);

            // DEBUG: log cart items before processing
            System.out.println("DEBUG: Cart has " + (cartItems == null ? 0 : cartItems.size()) + " item(s)");
            if (cartItems != null) {
                for (CartItem ci : cartItems) {
                    System.out.println("DEBUG: CartItem -> productId=" + ci.getProductId() + ", quantity=" + ci.getQuantity());
                }
            }

            if (cartItems == null || cartItems.isEmpty()) {
                return null; // No items to checkout
            }
            
            // Ensure user exists in database
            if (!userRepository.existsById(userId)) {
                // User doesn't exist, create temporary user
                User tempUser = new User();
                tempUser.setUserId(userId);
                tempUser.setFirstName("Anonymous");
                tempUser.setLastName("User");
                tempUser.setEmail("temp_" + System.currentTimeMillis() + "@example.com");
                tempUser.setPassword("temp");
                userRepository.save(tempUser);
                System.out.println("Created missing user during checkout: " + userId);
            }
            
            // Create new order
            Order order = new Order();
            String orderId = "ORDER_" + UUID.randomUUID().toString();
            order.setOrderId(orderId);
            order.setUserId(userId);
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
            
            // Set total amount and save order FIRST (before order items)
            order.setTotalAmount(totalAmount);
            orderRepository.save(order);
            
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
                    System.out.println("DEBUG orderItem: saving -> orderId=" + orderId + ", productId=" + orderItem.getProductId() + ", qty=" + orderItem.getQuantity());
                    orderItemRepository.save(orderItem);
                    System.out.println("DEBUG orderItem: saved -> orderItemId=" + orderItem.getOrderItemId());
                    
                    System.out.println("Created order item: " + product.getName() + 
                                     " x" + cartItem.getQuantity());
                }
            }
            
            // Clear cart after successful checkout
            cartService.clearCart(session);
            
            // Create and save payment record
            Payment payment = new Payment();
            payment.setPaymentId("PAY_" + UUID.randomUUID().toString());
            payment.setOrderId(orderId);
            payment.setAmount(totalAmount);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentStatus("completed"); // For simplicity, mark as completed
            payment.setTransactionId(UUID.randomUUID().toString()); // Generate a transaction ID
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCreatedDate(LocalDateTime.now());
            payment.setUpdatedDate(LocalDateTime.now());
            
            try {
                paymentRepository.save(payment);
                System.out.println("Payment record saved successfully: " + payment.getPaymentId());
            } catch (Exception paymentError) {
                System.out.println("Warning: Failed to save payment record: " + paymentError.getMessage());
                // Don't fail the checkout if payment record fails
            }

            // Send notification to customer about order completion
            try {
                notificationService.getNotification(orderId);
                System.out.println("Order completion notification sent to user: " + userId);
            } catch (Exception notificationError) {
                System.out.println("Warning: Failed to send notification: " + notificationError.getMessage());
                // Don't fail the checkout if notification fails
            }
            
            System.out.println("Checkout completed successfully!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Payment Method: " + paymentMethod);
            System.out.println("Total Amount: $" + totalAmount);
            System.out.println("Payment ID: " + payment.getPaymentId());
            
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