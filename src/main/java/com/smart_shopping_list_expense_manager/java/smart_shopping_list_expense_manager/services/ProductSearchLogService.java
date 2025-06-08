// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/services/ProductSearchLogService.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.ProductSearchLogDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductSearchLogEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductSearchLogRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductSearchLogService {
    private final ProductSearchLogRepository logRepo;
    private final ProductRepository        productRepo;
    private final UsersRepository          usersRepo;

    public ProductSearchLogService(
            ProductSearchLogRepository logRepo,
            ProductRepository productRepo,
            UsersRepository usersRepo
    ) {
        this.logRepo    = logRepo;
        this.productRepo = productRepo;
        this.usersRepo   = usersRepo;
    }

    public void recordSearch(UUID userId, ProductSearchLogDTO dto) {
        // load associated entities
        ProductEntity product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + dto.getProductId()));
        UsersEntity user = usersRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // build and save your existing ProductSearchLogEntity
        ProductSearchLogEntity log = new ProductSearchLogEntity();
        log.setProduct(product);
        log.setUser(user);
        log.setSearchTerm(dto.getSearchTerm());
        // createdAt is auto‐set by @PrePersist

        logRepo.save(log);
    }
}
