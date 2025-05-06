package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.StoreDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.StorePriceDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StoreEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StorePriceEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StoreRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StorePriceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    private final StoreRepository storeRepository;
    private final StorePriceRepository storePriceRepository;

    public StoreController(
            StoreRepository storeRepository,
            StorePriceRepository storePriceRepository
    ) {
        this.storeRepository      = storeRepository;
        this.storePriceRepository = storePriceRepository;
    }

    /**
     * GET /api/stores
     * → List of all stores as StoreDTO
     */
    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> dtos = storeRepository.findAll()
                .stream()
                .map(store -> {
                    StoreDTO dto = new StoreDTO();
                    dto.setId(store.getStoreId());
                    dto.setName(store.getName());
                    dto.setIcon(store.getIcon());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/stores/{storeId}
     * → Single StoreDTO by ID
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable UUID storeId) {
        return storeRepository.findById(storeId)
                .map(store -> {
                    StoreDTO dto = new StoreDTO();
                    dto.setId(store.getStoreId());
                    dto.setName(store.getName());
                    dto.setIcon(store.getIcon());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/stores/{storeId}/products
     * → All price‐tagged “products” for that store as StorePriceDTO
     */
    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<StorePriceDTO>> getProductsForStore(@PathVariable UUID storeId) {
        List<StorePriceDTO> dtos = storePriceRepository
                .findByStore_StoreId(storeId)
                .stream()
                .map(sp -> new StorePriceDTO(
                        sp.getStore().getStoreId(),
                        sp.getStore().getName(),
                        sp.getPrice()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
