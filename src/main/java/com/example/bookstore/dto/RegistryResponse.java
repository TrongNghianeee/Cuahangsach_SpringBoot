package com.example.bookstore.dto;

import lombok.Data;

@Data
public class RegistryResponse {
    private String message;
    private String error;

    public RegistryResponse(String message) {
        this.message = message;
    }

    public RegistryResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }
}