package com.example.bookstore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bookstore.dto.InventoryDTO;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.InventoryTransaction;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.InventoryTransactionRepository;
import com.example.bookstore.repository.UserRepository;

@Service
public class InventoryService {

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<InventoryTransaction> createInventoryTransactions(List<InventoryDTO> inventoryDTOs) {
        List<InventoryTransaction> transactions = new ArrayList<>();
        for (InventoryDTO inventoryDTO : inventoryDTOs) {
            Book book = bookRepository.findById(inventoryDTO.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại với ID: " + inventoryDTO.getBookId()));
            User user = userRepository.findById(inventoryDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại với ID: " + inventoryDTO.getUserId()));

            if (!"Nhập".equalsIgnoreCase(inventoryDTO.getTransactionType()) && 
                !"Xuất".equalsIgnoreCase(inventoryDTO.getTransactionType())) {
                throw new IllegalArgumentException("Loại giao dịch không hợp lệ: " + inventoryDTO.getTransactionType());
            }

            // Update book stock quantity based on transaction type
            if ("Nhập".equalsIgnoreCase(inventoryDTO.getTransactionType())) {
                book.setStockQuantity(book.getStockQuantity() + inventoryDTO.getQuantity());
            } else {
                if (book.getStockQuantity() < inventoryDTO.getQuantity()) {
                    throw new IllegalArgumentException("Số lượng tồn kho không đủ cho sách ID: " + inventoryDTO.getBookId());
                }
                book.setStockQuantity(book.getStockQuantity() - inventoryDTO.getQuantity());
            }

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setBook(book);
            transaction.setTransactionType(inventoryDTO.getTransactionType());
            transaction.setQuantity(inventoryDTO.getQuantity());
            transaction.setPriceAtTransaction(inventoryDTO.getPrice());
            transaction.setUser(user);
            transaction.setTransactionDate(LocalDateTime.now());

            bookRepository.save(book);
            transactions.add(inventoryTransactionRepository.save(transaction));
        }
        return transactions;
    }

    public InventoryTransaction getInventoryTransactionById(Integer id) {
        return inventoryTransactionRepository.findById(id)
                .orElse(null);
    }

    public List<InventoryTransaction> getAllInventoryTransactions() {
        return inventoryTransactionRepository.findAll();
    }

    @Transactional
    public InventoryTransaction updateInventoryTransaction(Integer id, InventoryDTO inventoryDTO) {
        Optional<InventoryTransaction> optionalTransaction = inventoryTransactionRepository.findById(id);
        if (!optionalTransaction.isPresent()) {
            return null;
        }

        InventoryTransaction transaction = optionalTransaction.get();
        Book book = bookRepository.findById(inventoryDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));
        User user = userRepository.findById(inventoryDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Revert previous stock changes
        if ("Nhập".equalsIgnoreCase(transaction.getTransactionType())) {
            book.setStockQuantity(book.getStockQuantity() - transaction.getQuantity());
        } else if ("Xuất".equalsIgnoreCase(transaction.getTransactionType())) {
            book.setStockQuantity(book.getStockQuantity() + transaction.getQuantity());
        }

        // Apply new stock changes
        if ("Nhập".equalsIgnoreCase(inventoryDTO.getTransactionType())) {
            book.setStockQuantity(book.getStockQuantity() + inventoryDTO.getQuantity());
        } else if ("Xuất".equalsIgnoreCase(inventoryDTO.getTransactionType())) {
            if (book.getStockQuantity() < inventoryDTO.getQuantity()) {
                throw new IllegalArgumentException("Số lượng tồn kho không đủ");
            }
            book.setStockQuantity(book.getStockQuantity() - inventoryDTO.getQuantity());
        } else {
            throw new IllegalArgumentException("Loại giao dịch không hợp lệ");
        }

        transaction.setBook(book);
        transaction.setTransactionType(inventoryDTO.getTransactionType());
        transaction.setQuantity(inventoryDTO.getQuantity());
        transaction.setPriceAtTransaction(inventoryDTO.getPrice());
        transaction.setUser(user);
        transaction.setTransactionDate(LocalDateTime.now());

        bookRepository.save(book);
        return inventoryTransactionRepository.save(transaction);
    }

    @Transactional
    public void deleteInventoryTransaction(Integer id) {
        Optional<InventoryTransaction> optionalTransaction = inventoryTransactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            InventoryTransaction transaction = optionalTransaction.get();
            Book book = transaction.getBook();

            // Revert stock changes
            if ("Nhập".equalsIgnoreCase(transaction.getTransactionType())) {
                book.setStockQuantity(book.getStockQuantity() - transaction.getQuantity());
            } else if ("Xuất".equalsIgnoreCase(transaction.getTransactionType())) {
                book.setStockQuantity(book.getStockQuantity() + transaction.getQuantity());
            }

            bookRepository.save(book);
            inventoryTransactionRepository.deleteById(id);
        }
    }
}