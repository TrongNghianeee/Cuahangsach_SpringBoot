package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.InventoryTransaction;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    
    @Query("SELECT it FROM InventoryTransaction it " +
           "LEFT JOIN FETCH it.book " +
           "LEFT JOIN FETCH it.user " +
           "ORDER BY it.transactionDate DESC")
    List<InventoryTransaction> findAllWithDetails();
    
    @Query("SELECT it FROM InventoryTransaction it " +
           "LEFT JOIN FETCH it.book " +
           "LEFT JOIN FETCH it.user " +
           "WHERE it.book.bookId = :bookId " +
           "ORDER BY it.transactionDate DESC")
    List<InventoryTransaction> findByBookIdWithDetails(@Param("bookId") Integer bookId);
    
    @Query("SELECT COALESCE(SUM(CASE WHEN it.transactionType = 'Nhập' THEN it.quantity ELSE -it.quantity END), 0) " +
           "FROM InventoryTransaction it WHERE it.book.bookId = :bookId")
    Integer calculateCurrentStock(@Param("bookId") Integer bookId);
}
