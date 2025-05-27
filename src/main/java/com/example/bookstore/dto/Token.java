package com.example.bookstore.dto;

import lombok.Data;

@Data
public class Token {
    private String access_token;
    private String token_type;

    public Token(String access_token, String token_type) {
        this.access_token = access_token;
        this.token_type = token_type;
    }
}