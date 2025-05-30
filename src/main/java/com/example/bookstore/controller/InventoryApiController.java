package com.example.bookstore.controller;

import com.example.bookstore.dto.InventoryDTO;
import com.example.bookstore.facade.ProductFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryApiController {

    @Autowired
    private ProductFacade productFacade;

    @PostMapping
    public ResponseEntity<List<InventoryDTO>> createInventoryTransaction(@Valid @RequestBody List<InventoryDTO> inventoryDTOs) {
        List<InventoryDTO> createdInventories = productFacade.createInventoryTransactions(inventoryDTOs);
        return ResponseEntity.ok(createdInventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryTransactionById(@PathVariable Integer id) {
        InventoryDTO inventoryDTO = productFacade.getInventoryTransactionByIdDTO(id);
        return ResponseEntity.ok(inventoryDTO);
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventoryTransactions() {
        List<InventoryDTO> inventoryDTOs = productFacade.getAllInventoryTransactionsDTO();
        return ResponseEntity.ok(inventoryDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> updateInventoryTransaction(@PathVariable Integer id, @Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryDTO updatedInventory = productFacade.updateInventoryTransaction(id, inventoryDTO);
        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryTransaction(@PathVariable Integer id) {
        productFacade.deleteInventoryTransaction(id);
        return ResponseEntity.noContent().build();
    }
}