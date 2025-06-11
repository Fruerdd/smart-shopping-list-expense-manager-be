package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.UserDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserDashboardController {
    private final UserDashboardService userService;

    public UserDashboardController(UserDashboardService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/favorite-products")
    public ResponseEntity<List<FavoriteProductDTO>> getFavoriteProducts(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getFavoriteProducts(id));
    }

    @PostMapping("/{id}/favorite-products/{productId}")
    public ResponseEntity<FavoriteProductDTO> addFavoriteProduct(@PathVariable UUID id, @PathVariable UUID productId) {
        return ResponseEntity.ok(userService.addFavoriteProduct(id, productId));
    }

    @DeleteMapping("/{id}/favorite-products/{productId}")
    public ResponseEntity<Void> removeFavoriteProduct(@PathVariable UUID id, @PathVariable UUID productId) {
        userService.removeFavoriteProduct(id, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/favorite-stores")
    public ResponseEntity<List<FavoriteStoreDTO>> getFavoriteStores(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getFavoriteStores(id));
    }

    @PostMapping("/{id}/favorite-stores/{storeId}")
    public ResponseEntity<FavoriteStoreDTO> addFavoriteStore(@PathVariable UUID id, @PathVariable UUID storeId) {
        return ResponseEntity.ok(userService.addFavoriteStore(id, storeId));
    }

    @DeleteMapping("/{id}/favorite-stores/{storeId}")
    public ResponseEntity<Void> removeFavoriteStore(@PathVariable UUID id, @PathVariable UUID storeId) {
        userService.removeFavoriteStore(id, storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stores/search")
    public ResponseEntity<List<StoreDTO>> searchStores(@PathVariable UUID id, @RequestParam String query) {
        return ResponseEntity.ok(userService.searchStores(id, query));
    }

    @GetMapping("/{id}/products/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@PathVariable UUID id, @RequestParam String query) {
        return ResponseEntity.ok(userService.searchProducts(id, query));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getAllProducts(id));
    }

    @GetMapping("/{id}/stores")
    public ResponseEntity<List<StoreDTO>> getAllStores(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getAllStores(id));
    }

    @GetMapping("/{id}/products/{productId}/prices")
    public ResponseEntity<List<StorePriceDTO>> getProductPrices(@PathVariable UUID id, @PathVariable UUID productId) {
        return ResponseEntity.ok(userService.getProductPrices(id, productId));
    }

    @GetMapping("/{id}/shopping-lists")
    public ResponseEntity<List<ShoppingListDTO>> getAllShoppingLists(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getAllShoppingLists(id));
    }

    @PostMapping("/{id}/shopping-lists")
    public ResponseEntity<ShoppingListDTO> createShoppingList(@PathVariable UUID id, @RequestBody ShoppingListDTO shoppingListDTO) {
        return ResponseEntity.ok(userService.createShoppingList(id, shoppingListDTO));
    }

    @PutMapping("/{id}/shopping-lists/{listId}")
    public ResponseEntity<ShoppingListDTO> updateShoppingList(@PathVariable UUID id, @PathVariable UUID listId, @RequestBody ShoppingListDTO shoppingListDTO) {
        return ResponseEntity.ok(userService.updateShoppingList(id, listId, shoppingListDTO));
    }

    @DeleteMapping("/{id}/shopping-lists/{listId}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable UUID id, @PathVariable UUID listId) {
        userService.deleteShoppingList(id, listId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/shopping-lists/{listId}/items/{itemId}")
    public ResponseEntity<ShoppingListItemDTO> updateShoppingListItem(@PathVariable UUID id, @PathVariable UUID listId, @PathVariable UUID itemId, @RequestBody ShoppingListItemDTO itemDTO) {
        return ResponseEntity.ok(userService.updateShoppingListItem(id, listId, itemId, itemDTO));
    }

    @PostMapping("/{id}/shopping-lists/{listId}/collaborators")
    public ResponseEntity<Void> addCollaborator(@PathVariable UUID id, @PathVariable UUID listId, @RequestBody CollaboratorDTO collaboratorDTO) {
        userService.addCollaborator(id, listId, collaboratorDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/shopping-lists/{listId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> updateCollaborator(@PathVariable UUID id, @PathVariable UUID listId, @PathVariable UUID collaboratorId, @RequestBody CollaboratorDTO collaboratorDTO) {
        userService.updateCollaborator(id, listId, collaboratorId, collaboratorDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/shopping-lists/{listId}/collaborators/{collaboratorId}")
    public ResponseEntity<Void> removeCollaborator(@PathVariable UUID id, @PathVariable UUID listId, @PathVariable UUID collaboratorId) {
        userService.removeCollaborator(id, listId, collaboratorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/loyalty/{userId}")
    public ResponseEntity<LoyaltyTierEnum> getLoyaltyTier(@PathVariable UUID userId) {
        LoyaltyTierEnum tier = userService.getUserLoyaltyTier(userId);
        return ResponseEntity.ok(tier);
    }
}