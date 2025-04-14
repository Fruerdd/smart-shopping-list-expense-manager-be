package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FavoriteProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProductEntity, UUID> {
}
