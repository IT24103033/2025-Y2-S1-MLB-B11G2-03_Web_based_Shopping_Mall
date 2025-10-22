package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already registered! Please use a different email.");
            model.addAttribute("user", user);
            return "register";
        }
        userService.registerUser(user);
        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "index";
    }


    // Login handled by Spring Security - override if needed

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String recoverPassword(@RequestParam String email) {
        userService.recoverPassword(email);
        return "redirect:/login?resetSent";
    }

    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
            return "profile";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            User existingUser = userService.findByEmail(email);
            existingUser.setFirst_name(user.getFirst_name());
            existingUser.setLast_name(user.getLast_name());
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            userService.updateUser(existingUser);
            return "redirect:/profile?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            System.out.println(e.getMessage());
            return "profile";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                 Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            userService.changePassword(email, oldPassword, newPassword);
            return "redirect:/profile?passwordChanged";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }

    @GetMapping("/logout-success")
    public String logoutSuccess(Model model) {
        model.addAttribute("success", "You have been logged out successfully.");
        return "index";
    }

}