package com.example.bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.dto.UserResponseDTO;
import com.example.bookstore.facade.BookstoreFacade;
import com.example.bookstore.model.User;

@RestController
@RequestMapping("/api/admin/users")
public class UserApiController {

    @Autowired
    private BookstoreFacade bookstoreFacade;    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<UserResponseDTO> users = bookstoreFacade.getAllUsersDTO();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("message", "Lấy danh sách người dùng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id) {
        try {
            User user = bookstoreFacade.getUserById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            response.put("message", "Lấy thông tin người dùng thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = bookstoreFacade.registerUser(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            response.put("message", "Thêm người dùng thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi thêm người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
        try {
            User user = bookstoreFacade.updateUser(id, userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            response.put("message", "Cập nhật người dùng thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Integer id, @RequestBody Map<String, String> statusRequest) {
        try {
            String status = statusRequest.get("status");
            if (status == null || (!status.equals("Active") && !status.equals("Lock"))) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Trạng thái không hợp lệ. Chỉ chấp nhận 'Active' hoặc 'Lock'");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            bookstoreFacade.toggleUserStatus(id, status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", status.equals("Lock") ? "Khóa tài khoản thành công" : "Mở khóa tài khoản thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi thay đổi trạng thái người dùng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<Map<String, Object>> lockUser(@PathVariable Integer id) {
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "Lock");
        return toggleUserStatus(id, statusRequest);
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<Map<String, Object>> unlockUser(@PathVariable Integer id) {
        Map<String, String> statusRequest = new HashMap<>();
        statusRequest.put("status", "Active");
        return toggleUserStatus(id, statusRequest);
    }
}
