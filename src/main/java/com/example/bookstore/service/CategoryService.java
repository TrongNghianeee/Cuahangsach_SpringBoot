package com.example.bookstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId).orElse(null);
    }

    public Category createCategory(String categoryName) {
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new RuntimeException("Tên loại sách đã tồn tại");
        }
        
        Category category = new Category();
        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer categoryId, String categoryName) {
        Category category = getCategoryById(categoryId);
        if (category != null) {
            // Kiểm tra trùng tên (trừ chính nó)
            if (categoryRepository.existsByCategoryName(categoryName) && 
                !category.getCategoryName().equals(categoryName)) {
                throw new RuntimeException("Tên loại sách đã tồn tại");
            }
            category.setCategoryName(categoryName);
            return categoryRepository.save(category);
        }
        return null;
    }

    public void deleteCategory(Integer categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword);
    }
}
