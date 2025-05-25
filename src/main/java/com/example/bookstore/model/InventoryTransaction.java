package com.example.bookstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "transaction_type", length = 10)
    private String transactionType; // Nhập, Xuất

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "price_at_transaction", precision = 10, scale = 2)
    private BigDecimal priceAtTransaction;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}