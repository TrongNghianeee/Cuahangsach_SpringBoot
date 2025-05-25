package com.example.bookstore.model;

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
@Table(name = "book_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(length = 255)
    private String description;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();
}