package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.CartItem;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Product;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.CartItemRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.ProductRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * CartService - handles all shopping cart operations
 * Uses Spring Security authentication for user identification
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
     * Get authenticated user ID from Spring Security
     * Returns null if user is not logged in (anonymous user)
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
                && !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            return userRepository.findByUsername(username)
                    .map(User::getUserId)
                    .orElse(null);
        }
        
        return null;
    }
    
    /**
     * Check if user is authenticated
     */
    public boolean isUserAuthenticated() {
        return getAuthenticatedUserId() != null;
    }
    
    /**
     * Add item to cart or update quantity if it already exists
     * Requires user to be logged in
     */
    public String addToCart(HttpSession session, String productId, int quantity) {
        try {
            // Get authenticated user ID
            Long userId = getAuthenticatedUserId();
            if (userId == null) {
                return "Please login to add items to cart";
            }
            
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
                return "Updated quantity in cart to " + existingItem.getQuantity();
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
                return "Added to cart successfully";
            }
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // If duplicate entry error, try to update instead
            Long userId = getAuthenticatedUserId();
            if (userId != null) {
                CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    existingItem.setUpdatedDate(LocalDateTime.now());
                    cartItemRepository.save(existingItem);
                    return "Item already in cart - updated quantity to " + existingItem.getQuantity();
                }
            }
            throw e; // Re-throw if we couldn't handle it
        }
    }
    
    // Update quantity of item in cart by product ID
    public String updateQuantity(HttpSession session, String productId, int quantity) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return "Please login to update cart";
        }
        
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
    
    // Update quantity of item in cart by cart item ID
    public String updateQuantityByCartItemId(HttpSession session, String cartItemId, int quantity) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return "Please login to update cart";
        }
        
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            return "Cart item not found";
        }
        
        // Verify item belongs to current user
        if (!cartItem.getUserId().equals(userId)) {
            return "Unauthorized: This item doesn't belong to your cart";
        }
        
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemRepository.delete(cartItem);
            return "Item removed from cart (quantity set to 0)";
        } else {
            // Update quantity
            cartItem.setQuantity(quantity);
            cartItem.setUpdatedDate(LocalDateTime.now());
            cartItemRepository.save(cartItem);
            return "Quantity updated to " + quantity + " for cart item: " + cartItemId;
        }
    }

    // Remove item from cart by product ID
    public String removeFromCart(HttpSession session, String productId) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return "Please login to remove items";
        }
        
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        if (cartItem == null) {
            return "Item not found in cart";
        }
        
        cartItemRepository.delete(cartItem);
        return "Item removed from cart";
    }
    
    // Remove item from cart by cart item ID
    public String removeFromCartByCartItemId(HttpSession session, String cartItemId) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return "Please login to remove items";
        }
        
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElse(null);
        if (cartItem == null) {
            return "Cart item not found";
        }
        
        // Verify item belongs to current user
        if (!cartItem.getUserId().equals(userId)) {
            return "Unauthorized: This item doesn't belong to your cart";
        }
        
        cartItemRepository.delete(cartItem);
        return "Item removed from cart successfully (Cart Item ID: " + cartItemId + ")";
    }

    // Get all items in user's cart
    public List<CartItem> getCartItems(HttpSession session) {
        Long userId = getAuthenticatedUserId();
        if (userId == null) {
            return List.of(); // Return empty list if not authenticated
        }
        return cartItemRepository.findByUserId(userId);
    }

    // Clear all items from cart (used after checkout)
    public void clearCart(HttpSession session) {
        Long userId = getAuthenticatedUserId();
        if (userId != null) {
            cartItemRepository.deleteByUserId(userId);
        }
    }
}