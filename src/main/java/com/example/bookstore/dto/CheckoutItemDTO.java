package com.example.bookstore.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutItemDTO {
    
    @NotNull(message = "ID sách không được để trống")
    private Integer bookId;
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
    
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
    
    // Optional fields for display purposes
    private String bookTitle;
    private String bookAuthor;
    
    // Constructor
    public CheckoutItemDTO() {}
    
    public CheckoutItemDTO(Integer bookId, Integer quantity, BigDecimal price) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
    }
    
    public CheckoutItemDTO(Integer bookId, Integer quantity, BigDecimal price, 
                          String bookTitle, String bookAuthor) {
        this.bookId = bookId;
        this.quantity = quantity;
        this.price = price;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
    }
    
    // Calculate subtotal
    public BigDecimal getSubtotal() {
        if (price != null && quantity != null) {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
