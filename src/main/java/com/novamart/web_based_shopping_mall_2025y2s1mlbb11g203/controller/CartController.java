package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.CartItem;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * CartController - handles all shopping cart web requests
 * Provides REST API endpoints for cart operations
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    /**
     * Add item to cart
     * POST /api/cart/add/{productId}?quantity=2
     */
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable String productId, 
                           @RequestParam(defaultValue = "1") int quantity,
                           HttpSession session) {
        try {
            String result = cartService.addToCart(session, productId, quantity);
            return result;
        } catch (Exception e) {
            return "Failed to add to cart: " + e.getMessage();
        }
    }
    
    /**
     * Update item quantity in cart
     * PUT /api/cart/update/{productId}?quantity=3
     */
    @PutMapping("/update/{productId}")
    public String updateQuantity(@PathVariable String productId,
                                @RequestParam int quantity,
                                HttpSession session) {
        try {
            String result = cartService.updateQuantity(session, productId, quantity);
            return result;
        } catch (Exception e) {
            return "Failed to update quantity: " + e.getMessage();
        }
    }
    
    /**
     * Remove item from cart
     * DELETE /api/cart/remove/{productId}
     */
    @DeleteMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable String productId,
                                HttpSession session) {
        try {
            String result = cartService.removeFromCart(session, productId);
            return result;
        } catch (Exception e) {
            return "Failed to remove from cart: " + e.getMessage();
        }
    }
    
    /**
     * View all items in cart
     * GET /api/cart
     */
    @GetMapping
    public List<CartItem> viewCart(HttpSession session) {
        try {
            return cartService.getCartItems(session);
        } catch (Exception e) {
            System.out.println("Failed to get cart items: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Clear all items from cart
     * DELETE /api/cart/clear
     */
    @DeleteMapping("/clear")
    public String clearCart(HttpSession session) {
        try {
            cartService.clearCart(session);
            return "Cart cleared successfully";
        } catch (Exception e) {
            return "Failed to clear cart: " + e.getMessage();
        }
    }
    
    /**
     * Get cart item count (useful for UI)
     * GET /api/cart/count
     */
    @GetMapping("/count")
    public int getCartCount(HttpSession session) {
        try {
            List<CartItem> items = cartService.getCartItems(session);
            return items.stream().mapToInt(CartItem::getQuantity).sum();
        } catch (Exception e) {
            return 0;
        }
    }
    
    // TESTING ENDPOINTS - Browser-friendly GET requests for testing - remove in production
    
    /**
     * Add item via GET (for browser testing only)
     * GET /api/cart/test-add/{productId}?quantity=1
     */
    @GetMapping("/test-add/{productId}")
    public String testAddToCart(@PathVariable String productId, 
                               @RequestParam(defaultValue = "1") int quantity,
                               HttpSession session) {
        try {
            String result = cartService.addToCart(session, productId, quantity);
            return "TEST ADD: " + result;
        } catch (Exception e) {
            return "TEST ADD FAILED: " + e.getMessage();
        }
    }
    
    /**
     * Remove item via GET (for browser testing only)
     * GET /api/cart/test-remove/{productId}
     */
    @GetMapping("/test-remove/{productId}")
    public String testRemoveFromCart(@PathVariable String productId, HttpSession session) {
        try {
            String result = cartService.removeFromCart(session, productId);
            return "TEST REMOVE: " + result;
        } catch (Exception e) {
            return "TEST REMOVE FAILED: " + e.getMessage();
        }
    }
}