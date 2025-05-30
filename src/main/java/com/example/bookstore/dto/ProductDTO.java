package com.example.bookstore.dto;
import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDTO {
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
    @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    private Integer stockQuantity;

    private List<BookImageDTO> images;

    private List<CategoryDTO> categories;

    // Thêm trường categoryIds để hỗ trợ payload
    @NotEmpty(message = "Danh sách ID danh mục không được để trống")
    private List<Integer> categoryIds;

    // Constructor mặc định cho deserialization
    public ProductDTO() {
    }
}