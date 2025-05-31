package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.PopularShopDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.StoreCreateUpdateDTO;  // ← import your DTO
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.StoreDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.StoreProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StoreEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StoreRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StorePriceRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.AnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreRepository storeRepository;
    private final StorePriceRepository storePriceRepository;
    private final AnalyticsService analyticsService;
    public StoreController(
            StoreRepository storeRepository,
            StorePriceRepository storePriceRepository,
            AnalyticsService analyticsService
    ) {
        this.storeRepository      = storeRepository;
        this.storePriceRepository = storePriceRepository;
        this.analyticsService     = analyticsService;
    }

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

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(
            @RequestBody StoreCreateUpdateDTO body
    ) {
        StoreEntity entity = new StoreEntity();
        entity.setName(body.getName());
        entity.setIcon(body.getIcon());         // ← use getIcon()
        entity.setLocation(body.getLocation());
        entity.setContact(body.getContact());

        StoreEntity saved = storeRepository.save(entity);

        StoreDTO dto = new StoreDTO();
        dto.setId(saved.getStoreId());
        dto.setName(saved.getName());
        dto.setIcon(saved.getIcon());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{storeId}")
    public ResponseEntity<StoreDTO> updateStore(
            @PathVariable UUID storeId,
            @RequestBody StoreCreateUpdateDTO body
    ) {
        return storeRepository.findById(storeId)
                .map(existing -> {
                    existing.setName(body.getName());
                    existing.setIcon(body.getIcon());     // ← use getIcon()
                    existing.setLocation(body.getLocation());
                    existing.setContact(body.getContact());

                    StoreEntity saved = storeRepository.save(existing);

                    StoreDTO dto = new StoreDTO();
                    dto.setId(saved.getStoreId());
                    dto.setName(saved.getName());
                    dto.setIcon(saved.getIcon());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<StoreProductDTO>> getProductsForStore(
            @PathVariable UUID storeId
    ) {
        List<StoreProductDTO> dtos = storePriceRepository
                .findByStore_StoreId(storeId)
                .stream()
                .map(sp -> new StoreProductDTO(
                        sp.getStorePriceId().toString(),
                        sp.getStore().getStoreId().toString(),
                        sp.getProduct().getProductId().toString(),
                        sp.getProduct().getName(),
                        sp.getProduct().getCategory().getName(),
                        sp.getProduct().getDescription(),
                        sp.getProduct().isActive(),
                        sp.getPrice().doubleValue(),
                        sp.getBarcode()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularShopDTO>> getPopularShops() {
        List<PopularShopDTO> list = analyticsService.getPopularStores();
        return ResponseEntity.ok(list);
    }
}
