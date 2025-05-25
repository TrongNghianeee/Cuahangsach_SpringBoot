package com.example.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer bookId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String author;

    @Column(length = 255)
    private String publisher;

    @Column(name = "publication_year")
    private Short publicationYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @ManyToMany
    @JoinTable(
        name = "categories_details",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "book")
    private List<BookImage> images;

    @OneToMany(mappedBy = "book")
    private List<InventoryTransaction> transactions;

    @OneToMany(mappedBy = "book")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "book")
    private List<ShoppingCart> cartItems;
}