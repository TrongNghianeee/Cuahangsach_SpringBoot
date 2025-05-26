package com.example.bookstore.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.model.User;
import com.example.bookstore.service.UserService;

@Component
public class BookstoreFacade {

    @Autowired
    private UserService userService;

    public User registerUser(UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    public User updateUser(Integer userId, UserDTO userDTO) {
        return userService.updateUser(userId, userDTO);
    }

    public void toggleUserStatus(Integer userId, String status) {
        userService.toggleUserStatus(userId, status);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public User getUserById(Integer userId) {
        return userService.getUserById(userId);
    }
}