package com.example.bookstore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bookstore.dto.BookWithStockDTO;
import com.example.bookstore.dto.InventoryDTO;
import com.example.bookstore.facade.BookstoreFacade;
import com.example.bookstore.model.Book;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/inventory")
public class InventoryController {

    @Autowired
    private BookstoreFacade bookstoreFacade;    @GetMapping
    public String showInventoryManagement(Model model) {
        try {
            // Lấy danh sách sách và tính toán stock thực tế
            List<Book> books = bookstoreFacade.getAllBooks();
            List<BookWithStockDTO> booksWithStock = books.stream()
                    .map(book -> {
                        Integer currentStock = bookstoreFacade.getCurrentStock(book.getBookId());
                        return new BookWithStockDTO(
                            book.getBookId(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getPrice(),
                            currentStock != null ? currentStock : 0
                        );
                    })
                    .collect(Collectors.toList());
            
            // Lấy lịch sử giao dịch
            List<InventoryDTO> transactions = bookstoreFacade.getAllInventoryTransactions();
            
            model.addAttribute("books", booksWithStock);
            model.addAttribute("transactions", transactions);
            model.addAttribute("inventoryDTO", new InventoryDTO());
            model.addAttribute("activePage", "Inventory");
            
            return "admin/Inventory";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("books", List.of());
            model.addAttribute("transactions", List.of());
            model.addAttribute("inventoryDTO", new InventoryDTO());
            model.addAttribute("activePage", "Inventory");
            return "admin/Inventory";
        }
    }

    @PostMapping("/process")
    public String processInventoryTransaction(@Valid @ModelAttribute InventoryDTO inventoryDTO, 
                                            BindingResult bindingResult,
                                            RedirectAttributes redirectAttributes,
                                            HttpSession session,
                                            Model model) {
        try {            // Kiểm tra validation errors
            if (bindingResult.hasErrors()) {
                List<Book> books = bookstoreFacade.getAllBooks();
                List<BookWithStockDTO> booksWithStock = books.stream()
                        .map(book -> {
                            Integer currentStock = bookstoreFacade.getCurrentStock(book.getBookId());
                            return new BookWithStockDTO(
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getPrice(),
                                currentStock != null ? currentStock : 0
                            );
                        })
                        .collect(Collectors.toList());
                List<InventoryDTO> transactions = bookstoreFacade.getAllInventoryTransactions();
                model.addAttribute("books", booksWithStock);
                model.addAttribute("transactions", transactions);
                model.addAttribute("error", "Vui lòng kiểm tra lại thông tin nhập vào");
                model.addAttribute("activePage", "Inventory");
                return "admin/Inventory";
            }
            
            // Kiểm tra thông tin bổ sung
            if (inventoryDTO.getBookId() == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn sách");
                return "redirect:/admin/inventory";
            }
            
            if (inventoryDTO.getTransactionType() == null || inventoryDTO.getTransactionType().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn phương thức nhập/xuất");
                return "redirect:/admin/inventory";
            }
            
            if (!inventoryDTO.getTransactionType().equals("Nhập") && !inventoryDTO.getTransactionType().equals("Xuất")) {
                redirectAttributes.addFlashAttribute("error", "Phương thức chỉ có thể là 'Nhập' hoặc 'Xuất'");
                return "redirect:/admin/inventory";
            }
            
            // Lấy userId từ session (giả sử đã login)
            // Trong thực tế, bạn sẽ lấy từ Spring Security context
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                // Fallback: sử dụng userId = 1 cho demo
                userId = 1;
            }
            
            // Thực hiện giao dịch
            bookstoreFacade.processInventoryTransaction(inventoryDTO, userId);
            
            String message = "Thực hiện " + inventoryDTO.getTransactionType().toLowerCase() + 
                           " kho thành công: " + inventoryDTO.getQuantity() + " cuốn";
            redirectAttributes.addFlashAttribute("message", message);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thực hiện giao dịch: " + e.getMessage());
        }
        
        return "redirect:/admin/inventory";
    }

    @GetMapping("/stock/{bookId}")
    @ResponseBody
    public Map<String, Object> getCurrentStockAjax(@PathVariable Integer bookId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer stock = bookstoreFacade.getCurrentStock(bookId);
            response.put("stock", stock != null ? stock : 0);
            response.put("success", true);
        } catch (Exception e) {
            response.put("stock", 0);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
}
