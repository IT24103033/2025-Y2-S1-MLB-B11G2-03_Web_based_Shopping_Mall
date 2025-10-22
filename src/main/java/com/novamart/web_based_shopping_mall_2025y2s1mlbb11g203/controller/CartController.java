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
     * Update item quantity in cart by cart item ID
     * PUT /api/cart/update?cartItemId=xxx&quantity=3
     * GET /api/cart/update?cartItemId=xxx&quantity=3 (for testing)
     */
    @RequestMapping(value = "/update", method = {RequestMethod.PUT, RequestMethod.GET})
    public String updateQuantity(@RequestParam String cartItemId,
                                @RequestParam int quantity,
                                HttpSession session) {
        try {
            String result = cartService.updateQuantityByCartItemId(session, cartItemId, quantity);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to update quantity: " + e.getMessage();
        }
    }
    
    /**
     * Update item quantity by product ID (alternative method)
     * PUT /api/cart/update-by-product/{productId}?quantity=3
     */
    @PutMapping("/update-by-product/{productId}")
    public String updateQuantityByProduct(@PathVariable String productId,
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
     * Remove item from cart by cart item ID
     * DELETE /api/cart/remove?cartItemId=xxx
     * GET /api/cart/remove?cartItemId=xxx (for testing)
     */
    @RequestMapping(value = "/remove", method = {RequestMethod.DELETE, RequestMethod.GET})
    public String removeFromCart(@RequestParam String cartItemId,
                                HttpSession session) {
        try {
            String result = cartService.removeFromCartByCartItemId(session, cartItemId);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to remove from cart: " + e.getMessage();
        }
    }
    
    /**
     * Remove item from cart by product ID (alternative method)
     * DELETE /api/cart/remove-by-product/{productId}
     */
    @DeleteMapping("/remove-by-product/{productId}")
    public String removeFromCartByProduct(@PathVariable String productId,
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
    public Object viewCart(HttpSession session) {
        try {
            // Check authentication status
            boolean isAuth = cartService.isUserAuthenticated();
            List<CartItem> items = cartService.getCartItems(session);
            
            // Return helpful debug info
            if (!isAuth) {
                return "Not authenticated - please login first. Cart will be empty until you login.";
            }
            
            if (items.isEmpty()) {
                return "Cart is empty (but you are logged in)";
            }
            
            return items;
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace
            return "Failed to get cart items: " + e.getMessage() + " | Error type: " + e.getClass().getName();
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
            // Check if user is authenticated first
            if (!cartService.isUserAuthenticated()) {
                return "ERROR: You must be logged in to add items to cart. Please login at /login";
            }
            
            String result = cartService.addToCart(session, productId, quantity);
            return "TEST ADD SUCCESS: " + result + " | Product ID: " + productId + " | Quantity: " + quantity;
        } catch (Exception e) {
            e.printStackTrace(); // Print full stack trace to console
            return "TEST ADD FAILED: " + e.getMessage() + " | Error type: " + e.getClass().getName();
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