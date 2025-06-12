package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.MoneySpentDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.CategorySpendDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.PriceAverageDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.StoreExpenseDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.SavingDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.UserAnalyticsService;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/profile")
public class UserAnalyticsController {

    private final UserProfileService userProfileService;
    private final UserAnalyticsService  analyticsService;

    public UserAnalyticsController(UserProfileService userProfileService,
                                   UserAnalyticsService analyticsService) {
        this.userProfileService   = userProfileService;
        this.analyticsService     = analyticsService;
    }

    private UUID currentUserId() {
        return userProfileService.getCurrent().getId();
    }

    @GetMapping("/money-spent")
    public ResponseEntity<List<MoneySpentDTO>> moneySpent() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateMoneySpent(userId)
        );
    }

    @GetMapping("/category-spending")
    public ResponseEntity<List<CategorySpendDTO>> categorySpending() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateCategorySpending(userId)
        );
    }

    @GetMapping("/price-averages")
    public ResponseEntity<List<PriceAverageDTO>> priceAverages() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculatePriceAverages(userId)
        );
    }

    @GetMapping("/store-expenses")
    public ResponseEntity<List<StoreExpenseDTO>> storeExpenses() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateStoreExpenses(userId)
        );
    }

    @GetMapping("/savings")
    public ResponseEntity<List<SavingDTO>> savings() {
        UUID userId = currentUserId();
        return ResponseEntity.ok(
                analyticsService.calculateSavings(userId)
        );
    }
}


