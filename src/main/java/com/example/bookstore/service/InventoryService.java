// package com.example.bookstore.service;

// import com.example.bookstore.dto.InventoryDTO;
// import com.example.bookstore.model.Book;
// import com.example.bookstore.model.InventoryTransaction;
// import com.example.bookstore.model.User;
// import com.example.bookstore.repository.InventoryTransactionRepository;
// import com.example.bookstore.repository.BookRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class InventoryService {
    
//     @Autowired
//     private InventoryTransactionRepository inventoryTransactionRepository;
    
//     @Autowired
//     private BookRepository bookRepository;
    
//     @Autowired
//     private BookService bookService;
    
//     @Autowired
//     private UserService userService;
    
//     /**
//      * Lấy tồn kho hiện tại của một cuốn sách
//      */
//     public Integer getCurrentStock(Integer bookId) {
//         Integer stock = inventoryTransactionRepository.calculateCurrentStock(bookId);
//         return stock != null ? stock : 0;
//     }
    
//     /**
//      * Thực hiện giao dịch nhập/xuất kho
//      */
//     @Transactional
//     public InventoryTransaction processInventoryTransaction(InventoryDTO inventoryDTO, Integer userId) {
//         // Kiểm tra sách tồn tại
//         Book book = bookService.getBookById(inventoryDTO.getBookId());
//         if (book == null) {
//             throw new RuntimeException("Không tìm thấy sách");
//         }
        
//         // Kiểm tra user tồn tại
//         User user = userService.getUserById(userId);
//         if (user == null) {
//             throw new RuntimeException("Không tìm thấy người dùng");
//         }
        
//         // Kiểm tra logic xuất kho
//         if ("Xuất".equals(inventoryDTO.getTransactionType())) {
//             Integer currentStock = getCurrentStock(inventoryDTO.getBookId());
//             if (currentStock < inventoryDTO.getQuantity()) {
//                 throw new RuntimeException("Số lượng xuất (" + inventoryDTO.getQuantity() + 
//                     ") vượt quá số lượng tồn kho (" + currentStock + ")");
//             }
//         }
        
//         // Tạo transaction
//         InventoryTransaction transaction = new InventoryTransaction();
//         transaction.setBook(book);
//         transaction.setUser(user);
//         transaction.setTransactionType(inventoryDTO.getTransactionType());
//         transaction.setQuantity(inventoryDTO.getQuantity());
//         transaction.setPriceAtTransaction(inventoryDTO.getPrice());
//         transaction.setTransactionDate(LocalDateTime.now());
        
//         // Lưu transaction
//         transaction = inventoryTransactionRepository.save(transaction);
        
//         // Cập nhật stock_quantity trong bảng books
//         updateBookStockQuantity(inventoryDTO.getBookId());
        
//         return transaction;
//     }
    
//     /**
//      * Cập nhật stock_quantity trong bảng books dựa trên tổng các giao dịch
//      */
//     @Transactional
//     public void updateBookStockQuantity(Integer bookId) {
//         Integer newStock = getCurrentStock(bookId);
//         Book book = bookService.getBookById(bookId);
//         if (book != null) {
//             book.setStockQuantity(newStock);
//             bookRepository.save(book);
//         }
//     }
    
//     /**
//      * Lấy danh sách tất cả giao dịch
//      */
//     public List<InventoryDTO> getAllTransactions() {
//         List<InventoryTransaction> transactions = inventoryTransactionRepository.findAllWithDetails();
//         return transactions.stream()
//             .map(this::convertToInventoryDTO)
//             .collect(Collectors.toList());
//     }
    
//     /**
//      * Lấy danh sách giao dịch theo sách
//      */
//     public List<InventoryDTO> getTransactionsByBookId(Integer bookId) {
//         List<InventoryTransaction> transactions = inventoryTransactionRepository.findByBookIdWithDetails(bookId);
//         return transactions.stream()
//             .map(this::convertToInventoryDTO)
//             .collect(Collectors.toList());
//     }
    
//     /**
//      * Chuyển đổi InventoryTransaction sang InventoryDTO
//      */
//     public InventoryDTO convertToInventoryDTO(InventoryTransaction transaction) {
//         InventoryDTO dto = new InventoryDTO();
//         dto.setTransactionId(transaction.getTransactionId());
//         dto.setBookId(transaction.getBook().getBookId());
//         dto.setBookTitle(transaction.getBook().getTitle());
//         dto.setTransactionType(transaction.getTransactionType());
//         dto.setQuantity(transaction.getQuantity());
//         dto.setPrice(transaction.getPriceAtTransaction());
//         dto.setTransactionDate(transaction.getTransactionDate());
//         if (transaction.getUser() != null) {
//             dto.setUsername(transaction.getUser().getUsername());
//         }
//         dto.setCurrentStock(getCurrentStock(transaction.getBook().getBookId()));
//         return dto;
//     }
    
//     /**
//      * Tạo InventoryDTO cho form với thông tin sách và tồn kho hiện tại
//      */
//     public InventoryDTO createInventoryDTOForBook(Integer bookId) {
//         Book book = bookService.getBookById(bookId);
//         if (book == null) {
//             return null;
//         }
        
//         InventoryDTO dto = new InventoryDTO();
//         dto.setBookId(bookId);
//         dto.setBookTitle(book.getTitle());
//         dto.setCurrentStock(getCurrentStock(bookId));
//         return dto;
//     }
// }
