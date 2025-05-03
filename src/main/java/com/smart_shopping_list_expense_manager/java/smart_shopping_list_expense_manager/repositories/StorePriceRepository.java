package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StorePriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface StorePriceRepository extends JpaRepository<StorePriceEntity, UUID> {
    List<StorePriceEntity> findByStore_StoreId(UUID storeId);
    List<StorePriceEntity> findByProduct_ProductId(UUID productId);
}