package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.ProductSearchLogDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.ProductSearchLogService;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.ShoppingListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {
    private final ShoppingListService shoppingListService;
    private final ProductSearchLogService productSearchLogService;
    private UsersRepository usersRepo;

    public ShoppingListController(
            ShoppingListService shoppingListService,
            ProductSearchLogService productSearchLogService,
            UsersRepository         usersRepo
    ) {
        this.shoppingListService     = shoppingListService;
        this.productSearchLogService = productSearchLogService;
        this.usersRepo               = usersRepo;
    }

    @PostMapping
    public ResponseEntity<ShoppingListDTO> createShoppingList(@Valid @RequestBody ShoppingListDTO shoppingListDTO) {
        return ResponseEntity.ok(shoppingListService.createShoppingList(shoppingListDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListDTO> getShoppingListById(@PathVariable UUID id) {
        return ResponseEntity.ok(shoppingListService.getShoppingListById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShoppingListDTO>> getShoppingListsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(shoppingListService.getShoppingListsByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingListDTO> updateShoppingList(@PathVariable UUID id, @Valid @RequestBody ShoppingListDTO shoppingListDTO) {
        return ResponseEntity.ok(shoppingListService.updateShoppingList(id, shoppingListDTO));
    }

    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDeleteShoppingList(@PathVariable UUID id) {
        shoppingListService.softDeleteShoppingList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/collaborators")
    public ResponseEntity<List<CollaboratorDTO>> getCollaborators(@PathVariable UUID id) {
        return ResponseEntity.ok(shoppingListService.getCollaborators(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(shoppingListService.getCategories());
    }

    @GetMapping("/sidebar-categories")
    public ResponseEntity<List<CategoryDTO>> getSidebarCategories() {
        return ResponseEntity.ok(shoppingListService.getSidebarCategories());
    }

    @GetMapping("/products")
    public ResponseEntity<List<ShoppingListItemDTO>> getAllProducts() {
        return ResponseEntity.ok(shoppingListService.getAllProducts());
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ShoppingListItemDTO>> searchProducts(@RequestParam String term) {
        return ResponseEntity.ok(shoppingListService.searchProducts(term));
    }

    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<List<ShoppingListItemDTO>> getProductsByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(shoppingListService.getProductsByCategory(categoryId));
    }

    @GetMapping("/items/{itemId}/price-comparisons")
    public ResponseEntity<List<StoreItemDTO>> getItemPriceComparisons(@PathVariable UUID itemId) {
        return ResponseEntity.ok(shoppingListService.getItemPriceComparisons(itemId));
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreDTO>> getAllAvailableStores() {
        return ResponseEntity.ok(shoppingListService.getAllAvailableStores());
    }

    @PostMapping("/search-logs")
    @ResponseStatus(HttpStatus.CREATED)
    public void logProductSearch(
            Authentication authentication,
            @RequestBody ProductSearchLogDTO dto
    ) {
        String email = authentication.getName();

        UsersEntity user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        productSearchLogService.recordSearch(user.getUserId(), dto);
    }

}