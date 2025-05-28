package com.example.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer categoryId;

    @NotBlank(message = "Tên loại sách không được để trống")
    @Size(min = 3, max = 50, message = "Tên loại sách phải từ 3 đến 50 ký tự")
    private String categoryName;
}
