package com.example.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.facade.BookstoreFacade;
import com.example.bookstore.model.User;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private BookstoreFacade bookstoreFacade;

    @GetMapping
    public String showUserManagement(Model model) {
        model.addAttribute("activePage", "Accounts");
        model.addAttribute("users", bookstoreFacade.getAllUsers());
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("editUser", null);
        return "admin/Accounts";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute UserDTO userDTO, Model model) {
        try {
            bookstoreFacade.registerUser(userDTO);
            model.addAttribute("message", "Thêm người dùng thành công");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("users", bookstoreFacade.getAllUsers());
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("editUser", null);
        return "admin/Accounts";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Integer id, Model model) {
        User user = bookstoreFacade.getUserById(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setAddress(user.getAddress());
        userDTO.setRole(user.getRole());
        userDTO.setStatus(user.getStatus());

        model.addAttribute("users", bookstoreFacade.getAllUsers());
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("editUser", userDTO);
        model.addAttribute("editUserId", id);
        return "admin/Accounts";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Integer id, @ModelAttribute UserDTO userDTO, Model model) {
        try {
            bookstoreFacade.updateUser(id, userDTO);
            model.addAttribute("message", "Cập nhật người dùng thành công");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("users", bookstoreFacade.getAllUsers());
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("editUser", null);
        return "admin/Accounts";
    }

    @PostMapping("/lock/{id}")
    public String lockUser(@PathVariable Integer id, Model model) {
        try {
            bookstoreFacade.toggleUserStatus(id, "Lock");
            model.addAttribute("message", "Khóa tài khoản thành công");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("users", bookstoreFacade.getAllUsers());
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("editUser", null);
        return "admin/Accounts";
    }

    @PostMapping("/unlock/{id}")
    public String unlockUser(@PathVariable Integer id, Model model) {
        try {
            bookstoreFacade.toggleUserStatus(id, "Active");
            model.addAttribute("message", "Mở khóa tài khoản thành công");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("users", bookstoreFacade.getAllUsers());
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("editUser", null);
        return "admin/Accounts";
    }
}