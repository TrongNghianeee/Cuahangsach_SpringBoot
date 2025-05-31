package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bookstore.model.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
    // Find all order details for a specific order
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId")
    List<OrderDetail> findByOrderId(@Param("orderId") Integer orderId);
    
    // Find order details by book ID
    @Query("SELECT od FROM OrderDetail od WHERE od.book.bookId = :bookId")
    List<OrderDetail> findByBookId(@Param("bookId") Integer bookId);
    
    // Count order details by order ID
    @Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.order.orderId = :orderId")
    Long countByOrderId(@Param("orderId") Integer orderId);
}
