package com.example.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.RegistryRequest;
import com.example.bookstore.dto.RegistryResponse;
import com.example.bookstore.dto.Token;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.facade.AuthFacade;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacade authFacade;

    @Autowired
    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginRequest loginRequest) {
        Token token = authFacade.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        UserDTO user = authFacade.getCurrentUser(token.replace("Bearer ", ""));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/registry")
    public ResponseEntity<RegistryResponse> registry(@RequestBody RegistryRequest request) {
        RegistryResponse response = authFacade.registry(request);
        if (response.getError() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        boolean success = authFacade.logout(token.replace("Bearer ", ""));
        if (success) {
            return ResponseEntity.ok("Đăng xuất thành công");
        }
        return ResponseEntity.badRequest().body("Đăng xuất thất bại");
    }
}