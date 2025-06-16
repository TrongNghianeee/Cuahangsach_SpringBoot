package com.example.bookstore.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookImageDTO {
    private Integer imageId;
    private Integer bookId;
    private String bookTitle; // Chỉ lấy title thay vì toàn bộ Book object
    private String imageUrl;
    private String description;
    private Boolean isPrimary;
    private LocalDateTime uploadedAt;

    // Constructor từ BookImage entity
    public BookImageDTO(com.example.bookstore.model.BookImage bookImage) {
        this.imageId = bookImage.getImageId();
        this.bookId = bookImage.getBook() != null ? bookImage.getBook().getBookId() : null;
        this.bookTitle = bookImage.getBook() != null ? bookImage.getBook().getTitle() : null;
        this.imageUrl = bookImage.getImageUrl();
        this.description = bookImage.getDescription();
        this.isPrimary = bookImage.getIsPrimary();
        this.uploadedAt = bookImage.getUploadedAt();
    }
}