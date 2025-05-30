package com.example.bookstore.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bookstore.dto.RegistryRequest;
import com.example.bookstore.dto.RegistryResponse;
import com.example.bookstore.dto.Token;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Token authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
                return new Token(accessToken, "bearer");
            }
        }
        return null;
    }

    public UserDTO getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (jwtUtil.validateToken(token, user.getUsername())) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                userDTO.setFullName(user.getFullName());
                userDTO.setPhone(user.getPhone());
                userDTO.setAddress(user.getAddress());
                userDTO.setRole(user.getRole());
                userDTO.setStatus(user.getStatus());
                return userDTO;
            }
        }
        return null;
    }

    public RegistryResponse registry(RegistryRequest request) {
        // Kiểm tra username đã tồn tại
        Optional<User> existingUserByUsername = userRepository.findByUsername(request.getUsername());
        if (existingUserByUsername.isPresent()) {
            return new RegistryResponse("Đăng ký thất bại", "Username đã tồn tại");
        }

        // Kiểm tra email đã tồn tại
        Optional<User> existingUserByEmail = userRepository.findByEmail(request.getEmail());
        if (existingUserByEmail.isPresent()) {
            return new RegistryResponse("Đăng ký thất bại", "Email đã tồn tại");
        }

        // Tạo user mới
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhone());
        newUser.setAddress(request.getAddress());
        newUser.setRole("KH"); // Set cứng role là KH
        newUser.setStatus("Active"); // Set cứng status là Active

        // Lưu user vào database
        userRepository.save(newUser);

        return new RegistryResponse("Đăng ký thành công");
    }

    public boolean logout(String token) {
        // For JWT tokens, we simply validate that the token is valid
        // In a production environment, you might want to implement token blacklisting
        try {
            String username = jwtUtil.extractUsername(token);
            if (username != null && jwtUtil.validateToken(token, username)) {
                // Token is valid, logout successful
                // In a more sophisticated implementation, you would add the token to a blacklist
                return true;
            }
        } catch (Exception e) {
            // Token is invalid or expired
            return false;
        }
        return false;
    }
}