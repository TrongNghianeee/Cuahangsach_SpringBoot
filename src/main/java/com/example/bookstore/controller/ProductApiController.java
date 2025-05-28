package com.example.bookstore.controller;

import com.example.bookstore.dto.CategoryDTO;
import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.facade.ProductFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Validated
public class ProductApiController {

    @Autowired
    private ProductFacade productFacade;

    // Product endpoints
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        try {
            List<ProductDTO> productDTOs = productFacade.getAllProductsDTO();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productDTOs);
            response.put("message", "Lấy danh sách sản phẩm thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Integer id) {
        try {
            ProductDTO productDTO = productFacade.getProductByIdDTO(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productDTO);
            response.put("message", "Lấy thông tin sản phẩm thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO createdProduct = productFacade.createProduct(productDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdProduct);
            response.put("message", "Thêm sản phẩm thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductDTO productDTO) {
        try {
            ProductDTO updatedProduct = productFacade.updateProduct(id, productDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedProduct);
            response.put("message", "Cập nhật sản phẩm thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Integer id) {
        try {
            productFacade.deleteProduct(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa sản phẩm thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Category endpoints (giữ nguyên từ phiên bản trước)
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

    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Map<String, String> request) {
        try {
            String categoryName = request.get("categoryName");
            if (categoryName == null || categoryName.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tên danh mục không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            CategoryDTO category = productFacade.createCategory(categoryName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);
            response.put("message", "Thêm danh mục thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi thêm danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String categoryName = request.get("categoryName");
            if (categoryName == null || categoryName.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tên danh mục không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            CategoryDTO category = productFacade.updateCategory(id, categoryName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", category);
            response.put("message", "Cập nhật danh mục thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Integer id) {
        try {
            productFacade.deleteCategory(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa danh mục thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi xóa danh mục: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}