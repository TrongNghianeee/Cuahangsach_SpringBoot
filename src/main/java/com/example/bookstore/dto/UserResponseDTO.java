package com.example.bookstore.dto;

import java.time.format.DateTimeFormatter;

import com.example.bookstore.model.User;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String role; // KH, Nvien, Qly
    private String status; // Active, Lock
    private String createdAt;

    // Constructor để chuyển đổi từ User sang UserResponseDTO
    public UserResponseDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
        this.status = "Active".equals(user.getStatus()) ? "Active" : "Lock";
        this.createdAt = user.getCreatedAt() != null 
            ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) 
            : null;
    }
}