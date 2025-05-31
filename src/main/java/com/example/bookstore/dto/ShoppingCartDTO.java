package com.example.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDTO {
    private Integer userId;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookDescription;
    private java.math.BigDecimal bookPrice;
    private Integer stockQuantity;
    
    // Constructor for basic cart item (only user and book IDs)
    public ShoppingCartDTO(Integer userId, Integer bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }
}
