package com.example.bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.CategoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.dto.UserResponseDTO;
import com.example.bookstore.facade.AuthFacade;
import com.example.bookstore.facade.ProductFacade;
import com.example.bookstore.facade.UserFacade;

@RestController
@RequestMapping("/api/user")
public class UserHomeController {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private AuthFacade authFacade;

    /**
     * GET /api/user/books - Lấy danh sách tất cả sách cho user homepage
     */
    @GetMapping("/books")
    public ResponseEntity<Map<String, Object>> getAllBooks() {
        try {
            List<ProductDTO> books = productFacade.getAllProductsDTO();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", books);
            response.put("message", "Lấy danh sách sách thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách sách: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/user/categories - Lấy danh sách tất cả danh mục cho user homepage
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        try {
            List<CategoryDTO> categories = productFacade.getAllCategoriesDTO();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            response.put("message", "Lấy danh sách danh mục thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/user/books/{id} - Lấy thông tin chi tiết một cuốn sách
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<Map<String, Object>> getBookById(@PathVariable Integer id) {
        try {
            ProductDTO book = productFacade.getProductByIdDTO(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", book);
            response.put("message", "Lấy thông tin sách thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin sách: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/user/categories/{id} - Lấy thông tin chi tiết một danh mục
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Integer id) {
        try {
            CategoryDTO category = productFacade.getCategoryByIdDTO(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);
            response.put("message", "Lấy thông tin danh mục thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }    /**
     * PUT /api/user/profile - Cập nhật thông tin hồ sơ người dùng
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader("Authorization") String token, 
            @RequestBody UserDTO userDTO) {
        try {
            // Lấy thông tin user hiện tại từ token
            UserDTO currentUser = authFacade.getCurrentUser(token.replace("Bearer ", ""));
            if (currentUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Token không hợp lệ");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Cập nhật thông tin user thông qua facade
            UserResponseDTO updatedUser = userFacade.updateUserProfile(currentUser.getUserId(), userDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "Cập nhật thông tin thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
