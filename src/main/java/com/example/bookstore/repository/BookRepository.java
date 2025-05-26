package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.categories ORDER BY b.bookId")
    List<Book> findAllWithCategories();
    
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories WHERE b.bookId = :bookId")
    Book findByIdWithCategories(Integer bookId);
    
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.images WHERE b.bookId IN :bookIds")
    List<Book> findBooksWithImages(List<Integer> bookIds);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
}
