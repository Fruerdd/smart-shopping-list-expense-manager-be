package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.MoneySpentDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.CategorySpendDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.PriceAverageDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.StoreExpenseDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.SavingDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.UserAnalyticsService;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class UserAnalyticsController {

    private final UserProfileService userProfileService;
    private final UserAnalyticsService  analyticsService;

    public UserAnalyticsController(UserProfileService userProfileService,
                                   UserAnalyticsService analyticsService) {
        this.userProfileService   = userProfileService;
        this.analyticsService     = analyticsService;
    }

    private UUID currentUserId() {
        // getCurrent() returns a UserDTO, which holds the UUID
        return userProfileService.getCurrent().getId();
    }

    // 1) total spent per month
    @GetMapping("/money-spent")
    public ResponseEntity<List<MoneySpentDTO>> moneySpent() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateMoneySpent(userId)
        );
    }

    // 2) % spent by category
    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendDTO>> categorySpending() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateCategorySpending(userId)
        );
    }

    // 3) average price per item
    @GetMapping("/price-averages")
    public ResponseEntity<List<PriceAverageDTO>> priceAverages() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculatePriceAverages(userId)
        );
    }

    // 4) % spent by store
    @GetMapping("/store-expenses")
    public ResponseEntity<List<StoreExpenseDTO>> storeExpenses() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateStoreExpenses(userId)
        );
    }

    // 5) savings per month
    @GetMapping("/savings")
    public ResponseEntity<List<SavingDTO>> savings() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateSavings(userId)
        );
    }
}


