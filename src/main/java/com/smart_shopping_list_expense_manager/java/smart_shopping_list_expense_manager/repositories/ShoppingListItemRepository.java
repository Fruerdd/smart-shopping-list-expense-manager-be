package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ShoppingListItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItemEntity, UUID> {
    @Modifying
    @Query("DELETE FROM ShoppingListItemEntity i WHERE i.shoppingList.id = :listId")
    void deleteByShoppingListId(UUID listId);

    long countByShoppingListOwnerUserId(UUID ownerId);


    List<ShoppingListItemEntity> findByShoppingListOwnerUserId(UUID ownerId);
}
