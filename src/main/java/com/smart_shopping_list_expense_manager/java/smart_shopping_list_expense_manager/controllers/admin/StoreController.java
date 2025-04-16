package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StoreEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StorePriceEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StoreRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StorePriceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    private final StoreRepository storeRepository;
    private final StorePriceRepository storePriceRepository;

    public StoreController(StoreRepository storeRepository, StorePriceRepository storePriceRepository) {
        this.storeRepository = storeRepository;
        this.storePriceRepository = storePriceRepository;
    }

    @GetMapping
    public ResponseEntity<List<StoreEntity>> getAllStores() {
        List<StoreEntity> stores = storeRepository.findAll();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreEntity> getStoreById(@PathVariable UUID storeId) {
        return storeRepository.findById(storeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<StorePriceEntity>> getProductsForStore(@PathVariable UUID storeId) {
        // Assuming you add a custom method in StorePriceRepository like:
        // List<StorePriceEntity> findByStore_StoreId(UUID storeId);
        List<StorePriceEntity> storeProducts = storePriceRepository.findByStore_StoreId(storeId);
        return ResponseEntity.ok(storeProducts);
    }
}


