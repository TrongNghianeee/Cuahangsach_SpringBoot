package com.example.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.OverviewDTO;
import com.example.bookstore.facade.BookstoreFacade;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @Autowired
    private BookstoreFacade bookstoreFacade;    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('Qly', 'ADMIN')")
    public ResponseEntity<OverviewDTO> getOverview() {
        try {
            OverviewDTO overview = bookstoreFacade.getOverviewDTO();
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            // Return default values if error occurs
            return ResponseEntity.ok(new OverviewDTO(0L, 0L, 0L));
        }
    }

    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('Qly', 'ADMIN')")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Dashboard API is working!");
    }

    @GetMapping("/public-test")
    public ResponseEntity<String> publicTestEndpoint() {
        return ResponseEntity.ok("Public endpoint is working!");
    }
}
