package com.example.bookstore.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;

@Component
public class BookstoreFacade {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO userDTO) {
        // Kiểm tra username và email đã tồn tại
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Chuyển DTO thành entity
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setRole("KH"); // Mặc định là khách hàng
        user.setStatus("Active");

        // Lưu vào database
        userRepository.save(user);
    }
}