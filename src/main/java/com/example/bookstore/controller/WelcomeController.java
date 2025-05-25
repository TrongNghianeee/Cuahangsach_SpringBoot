package com.example.bookstore.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {    @GetMapping("/welcome")
    public String welcome(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/dashboard"; 
            } else {
                return "redirect:/home";
            }
        }
        return "redirect:/login"; // Nếu chưa đăng nhập, quay về login
    }
}