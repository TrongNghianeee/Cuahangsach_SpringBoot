package com.example.bookstore.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.RegistryRequest;
import com.example.bookstore.dto.RegistryResponse;
import com.example.bookstore.dto.Token;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.service.AuthService;

@Component
public class AuthFacade {
    private final AuthService authService;

    @Autowired
    public AuthFacade(AuthService authService) {
        this.authService = authService;
    }

    public Token login(String username, String password) {
        return authService.authenticate(username, password);
    }

    public UserDTO getCurrentUser(String token) {
        return authService.getUserFromToken(token);
    }

    public RegistryResponse registry(RegistryRequest request) {
        return authService.registry(request);
    }

    public boolean logout(String token) {
        return authService.logout(token);
    }
}