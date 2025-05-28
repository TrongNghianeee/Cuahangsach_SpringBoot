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
    private String imageUrl;
    private String description;
    private Boolean isPrimary;
    private LocalDateTime uploadedAt;
}
