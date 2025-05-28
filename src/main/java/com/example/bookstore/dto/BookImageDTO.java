package com.example.bookstore.dto;

import lombok.Data;

@Data
public class BookImageDTO {
    private Integer imageId;
    private String imageUrl;
    private String description;
    private Boolean isPrimary;

    public BookImageDTO(com.example.bookstore.model.BookImage bookImage) {
        this.imageId = bookImage.getImageId();
        this.imageUrl = bookImage.getImageUrl();
        this.description = bookImage.getDescription();
        this.isPrimary = bookImage.getIsPrimary();
    }
}