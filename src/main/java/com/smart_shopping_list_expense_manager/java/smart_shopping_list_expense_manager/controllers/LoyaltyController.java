package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.LoyaltyDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/loyalty")
@CrossOrigin(origins = "http://localhost:4200")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/points/{userId}")
    public ResponseEntity<LoyaltyDTO> getLoyaltyStatus(@PathVariable UUID userId) {
        LoyaltyDTO dto = loyaltyService.getLoyaltyStatus(userId);
        return ResponseEntity.ok(dto);
    }
}
