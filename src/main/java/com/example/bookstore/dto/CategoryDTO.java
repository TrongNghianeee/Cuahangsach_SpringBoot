package com.example.bookstore.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Integer categoryId;
    private String categoryName;

    public CategoryDTO(com.example.bookstore.model.Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
    }
}