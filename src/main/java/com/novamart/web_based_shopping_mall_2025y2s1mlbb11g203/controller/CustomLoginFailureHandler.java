package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String errorMessage = "Invalid email or password!";

        // Customize error messages based on exception type
        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid email or password! Please check your credentials.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Your account has been disabled. Please contact support.";
        } else if (exception instanceof LockedException) {
            errorMessage = "Your account has been locked. Please contact support.";
        }

        // Store error message in session
        request.getSession().setAttribute("loginError", errorMessage);

        // Redirect to login page with error parameter
        response.sendRedirect("/login?error=true");
    }
}