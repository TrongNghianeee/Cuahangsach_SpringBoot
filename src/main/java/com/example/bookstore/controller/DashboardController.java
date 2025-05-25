package com.example.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activePage", "Overview");
        return "admin/overview";
    }

    @GetMapping("/admin/overview")
    public String pageA(Model model) {
        model.addAttribute("activePage", "Overview");
        return "admin/overview";
    }

    @GetMapping("/admin/Accounts")
    public String pageB(Model model) {
        model.addAttribute("activePage", "Accounts");
        return "admin/Accounts";
    }

    @GetMapping("/admin/Products")
    public String pageC(Model model) {
        model.addAttribute("activePage", "Products");
        return "admin/Products";
    }
}