package com.example.bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.CategoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.facade.ProductFacade;

@RestController
@RequestMapping("/api/user")
public class UserHomeController {

    @Autowired
    private ProductFacade productFacade;

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
    }
}
