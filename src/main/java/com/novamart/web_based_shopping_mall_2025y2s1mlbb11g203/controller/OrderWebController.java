package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.CartItem;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.CartService;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

/**
 * OrderWebController - handles web UI for cart and checkout pages
 * Provides HTML pages for shopping cart and order processing
 */
@Controller
public class OrderWebController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private CheckoutService checkoutService;
    
    /**
     * Display shopping cart page
     * GET /cart
     */
    @GetMapping("/cart")
    public String showCart(Model model, HttpSession session) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(session);
            
            // Calculate total amount
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalItems = 0;
            
            for (CartItem item : cartItems) {
                if (item.getProduct() != null) {
                    BigDecimal itemTotal = item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                    totalAmount = totalAmount.add(itemTotal);
                    totalItems += item.getQuantity();
                }
            }
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("isEmpty", cartItems.isEmpty());
            
            return "cart";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load cart: " + e.getMessage());
            model.addAttribute("cartItems", List.of());
            model.addAttribute("totalAmount", BigDecimal.ZERO);
            model.addAttribute("totalItems", 0);
            model.addAttribute("isEmpty", true);
            return "cart";
        }
    }
    
    /**
     * Display checkout page
     * GET /checkout
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model, HttpSession session) {
        try {
            // Get checkout summary
            CheckoutService.CheckoutSummary summary = checkoutService.getCheckoutSummary(session);
            
            if (summary == null || summary.getItems().isEmpty()) {
                return "redirect:/cart?message=Cart is empty";
            }
            
            model.addAttribute("checkoutSummary", summary);
            model.addAttribute("cartItems", summary.getItems());
            model.addAttribute("totalAmount", summary.getTotalAmount());
            model.addAttribute("itemCount", summary.getItemCount());
            
            return "checkout";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load checkout: " + e.getMessage());
            return "redirect:/cart?error=checkout_failed";
        }
    }
    
    /**
     * Process checkout form submission
     * POST /checkout/process-web
     */
    @PostMapping("/checkout/process-web")
    public String processWebCheckout(@RequestParam String customerName,
                                    @RequestParam String customerEmail,
                                    @RequestParam String customerPhone,
                                    @RequestParam String customerAddress,
                                    @RequestParam String paymentMethod,
                                    @RequestParam(required = false) String cardNumber,
                                    @RequestParam(required = false) String cardExpiry,
                                    @RequestParam(required = false) String cardCvv,
                                    HttpSession session,
                                    Model model) {
        try {
            // TODO: Store customer details (for now just log them)
            System.out.println("Customer Details:");
            System.out.println("Name: " + customerName);
            System.out.println("Email: " + customerEmail);
            System.out.println("Phone: " + customerPhone);
            System.out.println("Address: " + customerAddress);
            System.out.println("Payment Method: " + paymentMethod);
            
            if ("card".equals(paymentMethod)) {
                System.out.println("Card Details: " + cardNumber + " | " + cardExpiry + " | " + cardCvv);
            }
            
            // Process checkout using existing service
            String orderId = checkoutService.processCheckout(session, paymentMethod);
            
            if (orderId != null) {
                return "redirect:/order-success?orderId=" + orderId;
            } else {
                return "redirect:/checkout?error=checkout_failed";
            }
            
        } catch (Exception e) {
            System.out.println("Web checkout failed: " + e.getMessage());
            return "redirect:/checkout?error=" + e.getMessage();
        }
    }
    
    /**
     * Display order success page
     * GET /order-success
     */
    @GetMapping("/order-success")
    public String orderSuccess(@RequestParam String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "order-success";
    }
}