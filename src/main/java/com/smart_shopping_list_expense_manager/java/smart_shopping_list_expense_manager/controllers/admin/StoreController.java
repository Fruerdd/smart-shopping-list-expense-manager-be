package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.StoreCreateUpdateDTO;  // ← import your DTO
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.StoreDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StoreEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "http://localhost:4200")
public class StoreController {

    private final StoreRepository storeRepository;

    public StoreController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
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
}
