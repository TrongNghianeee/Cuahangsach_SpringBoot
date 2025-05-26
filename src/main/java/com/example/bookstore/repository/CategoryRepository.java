package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);
    
    boolean existsByCategoryName(String categoryName);
}
