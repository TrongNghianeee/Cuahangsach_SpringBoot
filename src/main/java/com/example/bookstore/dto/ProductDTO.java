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
    private List<Integer> categoryIds;
    private List<String> categoryNames;
    private String primaryImageUrl;
    private List<String> imageUrls;
    
    // Constructor cho việc thêm sách mới (không có bookId)
    public ProductDTO(String title, String author, String publisher, Short publicationYear, 
                     String description, BigDecimal price, List<Integer> categoryIds, 
                     String primaryImageUrl) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.description = description;
        this.price = price;
        this.categoryIds = categoryIds;
        this.primaryImageUrl = primaryImageUrl;
    }
}
