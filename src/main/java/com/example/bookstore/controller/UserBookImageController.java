package com.example.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.BookImageDTO;
import com.example.bookstore.service.BookImageService;

@RestController
@RequestMapping("/api/user/book-images")
public class UserBookImageController {

    @Autowired
    private BookImageService bookImageService;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<BookImageDTO>>> getBookImagesForUser(@PathVariable Integer bookId) {
        try {
            List<BookImageDTO> images = bookImageService.getImagesDTOByBookId(bookId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách ảnh thành công", images));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi lấy danh sách ảnh: " + e.getMessage(), null));
        }
    }

    @GetMapping("/book/{bookId}/primary")
    public ResponseEntity<ApiResponse<BookImageDTO>> getPrimaryImageForUser(@PathVariable Integer bookId) {
        try {
            List<BookImageDTO> images = bookImageService.getImagesDTOByBookId(bookId);
            BookImageDTO primaryImage = images.stream()
                    .filter(img -> img.getIsPrimary() != null && img.getIsPrimary())
                    .findFirst()
                    .orElse(images.isEmpty() ? null : images.get(0)); // Fallback to first image if no primary
            
            if (primaryImage != null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Lấy ảnh chính thành công", primaryImage));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(false, "Không tìm thấy ảnh", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi lấy ảnh chính: " + e.getMessage(), null));
        }
    }
}
