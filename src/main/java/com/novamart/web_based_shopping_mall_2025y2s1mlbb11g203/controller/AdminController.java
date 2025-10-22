package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;


     
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        // Get statistics
        long totalUsers = userService.getTotalUsers();

        // Add attributes to model
        model.addAttribute("totalUsers", totalUsers);


        // Populate full users list for Users tab
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);

        model.addAttribute("adminName", "Admin User"); // Get from session

        return "admin-dashboard";
    }

    /**
     * Get all users (for AJAX requests)
     */
    @GetMapping("/users")
    @ResponseBody
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream().map(UserDto::from).toList();
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return "User deleted successfully";
        } catch (Exception e) {
            return "Error deleting user: " + e.getMessage();
        }
    }

    public static class UserDto {
        public Long id;
        public String first_name;
        public String last_name;
        public String email;
        public String username;
        public String role;

        public static UserDto from(User u) {
            UserDto d = new UserDto();
            d.id = u.getId();
            d.first_name = u.getFirst_name();
            d.last_name = u.getLast_name();
            d.email = u.getEmail();
            d.username = u.getUsername();
            d.role = u.getRole() != null ? u.getRole().name() : null;
            return d;
        }
    }

    // Handle edit request (show edit form)
    @GetMapping("/editUser")
    public String showEditForm(@RequestParam("userId") Long userId, Model model) {
        try {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "editUser";
        } catch (Exception e) {
            model.addAttribute("error", "User not found");
            return "redirect:/admin/dashboard";
        }
    }

//    // Handle delete request
//    @DeleteMapping("/user")
//    public String deleteUser(@RequestParam("userId") Long userId) {
//        userService.deleteUser(userId);
//        return "redirect:/admin/dashboard"; // Redirect back to dashboard
//    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, Model model) {
        try {
            // Check if email is already in use by another user
            if (userService.existsByEmail(user.getEmail())) {
                User existingUser = userService.findByEmail(user.getEmail());
                if (!existingUser.getId().equals(user.getId())) {
                    model.addAttribute("error", "Email already in use by another user");
                    model.addAttribute("user", user);
                    return "editUser";
                }
            }
            
            userService.saveUser(user);
            return "redirect:/admin/dashboard?success=User updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating user: " + e.getMessage());
            model.addAttribute("user", user);
            return "editUser";
        }
    }
}