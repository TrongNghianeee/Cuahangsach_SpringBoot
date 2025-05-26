package com.example.bookstore.dto;

import java.math.BigDecimal;

public class BookWithStockDTO {
    private Integer bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer currentStock;

    // Constructors
    public BookWithStockDTO() {}

    public BookWithStockDTO(Integer bookId, String title, String author, BigDecimal price, Integer currentStock) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.currentStock = currentStock;
    }

    // Getters and Setters
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public String getDisplayText() {
        return title + " - " + author;
    }
}
