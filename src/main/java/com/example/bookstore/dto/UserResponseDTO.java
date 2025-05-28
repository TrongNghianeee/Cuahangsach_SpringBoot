package com.example.bookstore.dto;

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
}
