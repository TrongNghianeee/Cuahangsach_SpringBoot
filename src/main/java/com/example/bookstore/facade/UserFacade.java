package com.example.bookstore.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.bookstore.dto.ShoppingCartDTO;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.dto.UserResponseDTO;
import com.example.bookstore.model.User;
import com.example.bookstore.service.ShoppingCartService;
import com.example.bookstore.service.UserService;

@Component
public class UserFacade {
    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

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

    // Cập nhật thông tin hồ sơ user (không cho phép thay đổi role và status)
    public UserResponseDTO updateUserProfile(Integer userId, UserDTO userDTO) {
        // Đảm bảo không thể thay đổi role và status từ user profile
        userDTO.setRole(null);
        userDTO.setStatus(null);
        userDTO.setUserId(userId);
        
        User user = userService.updateUser(userId, userDTO);
        return new UserResponseDTO(user);
    }

    // Thay đổi trạng thái user
    public void toggleUserStatus(Integer id, String status) {
        userService.toggleUserStatus(id, status);
    }

    // Shopping Cart Methods with Response Entity logic

    // Lấy danh sách sách trong giỏ hàng của user với response handling
    public ResponseEntity<Map<String, Object>> getUserShoppingCartResponse(Integer userId) {
        try {
            List<ShoppingCartDTO> cartItems = shoppingCartService.getCartByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", cartItems);
            response.put("message", "Lấy giỏ hàng thành công");
            response.put("totalItems", cartItems.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy giỏ hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Thêm sách vào giỏ hàng với response handling
    public ResponseEntity<Map<String, Object>> addBookToCartResponse(Integer userId, Integer bookId) {
        try {
            // Kiểm tra xem sách đã có trong giỏ hàng chưa
            if (shoppingCartService.isBookInCart(userId, bookId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Sách đã có trong giỏ hàng");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            ShoppingCartDTO cartItem = shoppingCartService.addToCart(userId, bookId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", cartItem);
            response.put("message", "Thêm sách vào giỏ hàng thành công");
            
            // Thêm thông tin tổng số sách trong giỏ hàng
            Long totalItems = shoppingCartService.getCartItemCount(userId);
            response.put("totalItems", totalItems);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi thêm sách vào giỏ hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Xóa sách khỏi giỏ hàng với response handling
    public ResponseEntity<Map<String, Object>> removeBookFromCartResponse(Integer userId, Integer bookId) {
        try {
            // Kiểm tra xem sách có trong giỏ hàng không
            if (!shoppingCartService.isBookInCart(userId, bookId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Sách không có trong giỏ hàng");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            shoppingCartService.removeFromCart(userId, bookId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa sách khỏi giỏ hàng thành công");
            
            // Thêm thông tin tổng số sách còn lại trong giỏ hàng
            Long totalItems = shoppingCartService.getCartItemCount(userId);
            response.put("totalItems", totalItems);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi xóa sách khỏi giỏ hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Basic shopping cart methods for internal use
    public List<ShoppingCartDTO> getUserShoppingCart(Integer userId) {
        return shoppingCartService.getCartByUserId(userId);
    }

    public ShoppingCartDTO addBookToCart(Integer userId, Integer bookId) {
        return shoppingCartService.addToCart(userId, bookId);
    }

    public void removeBookFromCart(Integer userId, Integer bookId) {
        shoppingCartService.removeFromCart(userId, bookId);
    }

    public void clearUserCart(Integer userId) {
        shoppingCartService.clearCart(userId);
    }

    public Long getCartItemCount(Integer userId) {
        return shoppingCartService.getCartItemCount(userId);
    }

    public boolean isBookInUserCart(Integer userId, Integer bookId) {
        return shoppingCartService.isBookInCart(userId, bookId);
    }
}