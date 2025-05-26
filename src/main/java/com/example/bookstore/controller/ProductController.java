package com.example.bookstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bookstore.dto.ProductDTO;
import com.example.bookstore.facade.BookstoreFacade;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private BookstoreFacade bookstoreFacade;

    @GetMapping
    public String showProductManagement(Model model) {
        try {
            List<Book> books = bookstoreFacade.getAllBooks();
            List<Category> categories = bookstoreFacade.getAllCategories();
            
            model.addAttribute("books", books);
            model.addAttribute("categories", categories);
            model.addAttribute("productDTO", new ProductDTO());
            model.addAttribute("categoryName", "");
            
            return "admin/Products";        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("books", List.of());
            model.addAttribute("categories", List.of());
            model.addAttribute("productDTO", new ProductDTO());
            model.addAttribute("categoryName", "");
            return "admin/Products";
        }
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String categoryName, RedirectAttributes redirectAttributes) {
        try {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên loại sách không được để trống");
                return "redirect:/admin/products";
            }
            
            bookstoreFacade.createCategory(categoryName.trim());
            redirectAttributes.addFlashAttribute("message", "Thêm loại sách thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm loại sách: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute ProductDTO productDTO, RedirectAttributes redirectAttributes) {
        try {
            if (productDTO.getTitle() == null || productDTO.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên sách không được để trống");
                return "redirect:/admin/products";
            }
            
            if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Giá sách phải lớn hơn 0");
                return "redirect:/admin/products";
            }
            
            bookstoreFacade.createBook(productDTO);
            redirectAttributes.addFlashAttribute("message", "Thêm sách thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sách: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Integer id, Model model) {
        try {
            Book book = bookstoreFacade.getBookById(id);
            if (book == null) {
                model.addAttribute("error", "Không tìm thấy sách");
                return "redirect:/admin/products";
            }
            
            List<Category> categories = bookstoreFacade.getAllCategories();
            ProductDTO editBook = bookstoreFacade.convertToProductDTO(book);
            
            model.addAttribute("books", bookstoreFacade.getAllBooks());
            model.addAttribute("categories", categories);
            model.addAttribute("productDTO", new ProductDTO());
            model.addAttribute("categoryName", "");
            model.addAttribute("editBook", editBook);
            model.addAttribute("editBookId", id);
            
            return "admin/Products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin sách: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable Integer id, @ModelAttribute ProductDTO productDTO, 
                           RedirectAttributes redirectAttributes) {
        try {
            if (productDTO.getTitle() == null || productDTO.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên sách không được để trống");
                return "redirect:/admin/products";
            }
            
            if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "Giá sách phải lớn hơn 0");
                return "redirect:/admin/products";
            }
            
            Book updatedBook = bookstoreFacade.updateBook(id, productDTO);
            if (updatedBook != null) {
                redirectAttributes.addFlashAttribute("message", "Cập nhật sách thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sách để cập nhật");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật sách: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookstoreFacade.deleteBook(id);
            redirectAttributes.addFlashAttribute("message", "Xóa sách thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sách: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}
