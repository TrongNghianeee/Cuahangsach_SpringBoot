package com.example.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer bookId;
    private String title;
    private String author;
    private String publisher;
    private Short publicationYear;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private List<BookImageDTO> images;
    private List<CategoryDTO> categories;
}
