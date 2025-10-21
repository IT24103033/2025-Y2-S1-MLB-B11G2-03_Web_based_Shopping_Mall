package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.CartItem;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Product;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.CartItemRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.ProductRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * CartService - handles all shopping cart operations
 * Uses session to manage anonymous users until they log in
 */
@Service
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get or create a user ID from session (handles anonymous users)
     * Creates temporary user in database to satisfy foreign key constraints
     */
    public String getOrCreateUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            // Create temporary user ID for anonymous shopping
            userId = "TEMP_" + UUID.randomUUID().toString();
            session.setAttribute("userId", userId);
            
            // Create temporary user in database
            User tempUser = new User();
            tempUser.setUserId(userId);
            tempUser.setFirstName("Anonymous");
            tempUser.setLastName("User");
            tempUser.setEmail("temp_" + System.currentTimeMillis() + "@example.com");
            tempUser.setPassword("temp");
            userRepository.save(tempUser);
            
            System.out.println("Created temporary user in database: " + userId);
        }
        return userId;
    }
    
    /**
     * Add item to cart or update quantity if it already exists
     */
    public String addToCart(HttpSession session, String productId, int quantity) {
        String userId = getOrCreateUserId(session);
        
        // Check if product exists
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "Product not found";
        }
        
        // Check if item already in cart
        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        
        if (existingItem != null) {
            // Update existing item quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedDate(LocalDateTime.now());
            cartItemRepository.save(existingItem);
            return "Updated quantity in cart";
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setCartItemId(UUID.randomUUID().toString());
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setCreatedDate(LocalDateTime.now());
            newItem.setUpdatedDate(LocalDateTime.now());
            
            cartItemRepository.save(newItem);
            return "Added to cart";
        }
    }
    
    /**
     * Update quantity of item in cart
     */
    public String updateQuantity(HttpSession session, String productId, int quantity) {
        String userId = getOrCreateUserId(session);
        
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (cartItem == null) {
            return "Item not found in cart";
        }
        
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemRepository.delete(cartItem);
            return "Item removed from cart";
        } else {
            // Update quantity
            cartItem.setQuantity(quantity);
            cartItem.setUpdatedDate(LocalDateTime.now());
            cartItemRepository.save(cartItem);
            return "Quantity updated";
        }
    }
    
    /**
     * Remove item from cart
     */
    public String removeFromCart(HttpSession session, String productId) {
        String userId = getOrCreateUserId(session);
        
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (cartItem == null) {
            return "Item not found in cart";
        }
        
        cartItemRepository.delete(cartItem);
        return "Item removed from cart";
    }
    
    /**
     * Get all items in user's cart
     */
    public List<CartItem> getCartItems(HttpSession session) {
        String userId = getOrCreateUserId(session);
        return cartItemRepository.findByUserId(userId);
    }
    
    /**
     * Clear all items from cart (used after checkout)
     */
    public void clearCart(HttpSession session) {
        String userId = getOrCreateUserId(session);
        cartItemRepository.deleteByUserId(userId);
    }
}