package com.example.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.facade.BookstoreFacade;

import jakarta.validation.Valid;

@Controller
public class RegisterController {

    @Autowired
    private BookstoreFacade bookstoreFacade;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid UserDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            bookstoreFacade.registerUser(userDTO);
            return "redirect:/login?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}