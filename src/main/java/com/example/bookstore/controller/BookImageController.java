package com.example.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.bookstore.dto.ApiResponse;
import com.example.bookstore.dto.BookImageDTO;
import com.example.bookstore.model.BookImage;
import com.example.bookstore.service.BookImageService;

@RestController
@RequestMapping("/api/admin/book-images")
public class BookImageController {

    @Autowired
    private BookImageService bookImageService;    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('Qly', 'Nvien')")
    public ResponseEntity<ApiResponse<List<BookImageDTO>>> getBookImages(@PathVariable Integer bookId) {
        try {
            List<BookImageDTO> images = bookImageService.getImagesDTOByBookId(bookId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách ảnh thành công", images));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi lấy danh sách ảnh: " + e.getMessage(), null));
        }
    }    @PostMapping("/book/{bookId}/upload")
    @PreAuthorize("hasAnyRole('Qly', 'Nvien')")
    public ResponseEntity<ApiResponse<BookImageDTO>> uploadBookImage(
            @PathVariable Integer bookId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary) {
        try {
            BookImage image = bookImageService.uploadBookImage(bookId, file, description, isPrimary);
            BookImageDTO imageDTO = new BookImageDTO(image);
            return ResponseEntity.ok(new ApiResponse<>(true, "Upload ảnh thành công", imageDTO));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi upload ảnh: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{imageId}/set-primary")
    @PreAuthorize("hasAnyRole('Qly', 'Nvien')")
    public ResponseEntity<ApiResponse<Void>> setPrimaryImage(@PathVariable Integer imageId) {
        try {
            bookImageService.setPrimaryImage(imageId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đặt ảnh chính thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi đặt ảnh chính: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('Qly', 'Nvien')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Integer imageId) {
        try {
            bookImageService.deleteImage(imageId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa ảnh thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Lỗi khi xóa ảnh: " + e.getMessage(), null));
        }
    }
}
