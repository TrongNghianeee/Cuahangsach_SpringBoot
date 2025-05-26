package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.BookImage;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, Integer> {
    
    List<BookImage> findByBookBookId(Integer bookId);
    
    BookImage findByBookBookIdAndIsPrimaryTrue(Integer bookId);
    
    void deleteByBookBookId(Integer bookId);
}
