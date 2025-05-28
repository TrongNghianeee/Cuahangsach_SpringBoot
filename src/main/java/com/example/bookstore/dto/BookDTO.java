package com.example.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Integer bookId;
    
    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 255, message = "Tên sách không quá 255 ký tự")
    private String title;
    
    @Size(max = 255, message = "Tác giả không quá 255 ký tự")
    private String author;
    
    @Size(max = 255, message = "Nhà xuất bản không quá 255 ký tự")
    private String publisher;
    
    private Short publicationYear;
    
    private String description;
    
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
    
    private Integer stockQuantity;
    
    private List<BookImageDTO> images;
    
    private List<CategoryDTO> categories;
    
    // Helper method to get category IDs
    public List<Integer> getCategoryIds() {
        if (categories == null) return null;
        return categories.stream()
            .map(CategoryDTO::getCategoryId)
            .toList();
    }
    
    // Helper method to get category names as comma-separated string
    public String getCategoryNames() {
        if (categories == null || categories.isEmpty()) return "";
        return categories.stream()
            .map(CategoryDTO::getCategoryName)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }
}
