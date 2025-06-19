package com.example.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.bookstore.model.InventoryTransaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {

    @NotNull(message = "Vui lòng chọn sách")
    private Integer bookId;
    
    // Thêm tên sách để hiển thị
    private String bookTitle;

    @NotBlank(message = "Vui lòng chọn phương thức nhập/xuất")
    private String transactionType;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Vui lòng chọn người dùng")
    private Integer userId;
      // Thêm username để hiển thị
    private String username;
    
    // Thêm thông tin thời gian giao dịch
    private LocalDateTime transactionDate;

    // Constructor từ InventoryTransaction entity
    public InventoryDTO(InventoryTransaction transaction) {
        this.bookId = transaction.getBook().getBookId();
        this.bookTitle = transaction.getBook().getTitle();
        this.transactionType = transaction.getTransactionType();
        this.quantity = transaction.getQuantity();        this.price = transaction.getPriceAtTransaction();
        this.userId = transaction.getUser().getUserId();
        this.username = transaction.getUser().getUsername();
        this.transactionDate = transaction.getTransactionDate();
    }

    // Constructor cho việc tạo mới (không có entity)
    public InventoryDTO(Integer bookId, String transactionType, Integer quantity, BigDecimal price, Integer userId) {
        this.bookId = bookId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.userId = userId;
    }
}