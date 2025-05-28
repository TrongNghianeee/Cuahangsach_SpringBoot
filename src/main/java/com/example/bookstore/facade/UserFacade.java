package com.example.bookstore.facade;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.dto.UserResponseDTO;
import com.example.bookstore.model.User;
import com.example.bookstore.service.UserService;

@Component
public class UserFacade {
    @Autowired
    private UserService userService;

    // Lấy danh sách tất cả user dưới dạng DTO
    public List<UserResponseDTO> getAllUsersDTO() {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Lấy user theo ID dưới dạng DTO
    public UserResponseDTO getUserByIdDTO(Integer id) {
        User user = userService.getUserById(id);
        return new UserResponseDTO(user);
    }

    // Tạo user mới
    public UserResponseDTO registerUser(UserDTO userDTO) {
        User user = userService.createUser(userDTO);
        return new UserResponseDTO(user);
    }

    // Cập nhật user
    public UserResponseDTO updateUser(Integer id, UserDTO userDTO) {
        User user = userService.updateUser(id, userDTO);
        return new UserResponseDTO(user);
    }

    // Thay đổi trạng thái user
    public void toggleUserStatus(Integer id, String status) {
        userService.toggleUserStatus(id, status);
    }
}