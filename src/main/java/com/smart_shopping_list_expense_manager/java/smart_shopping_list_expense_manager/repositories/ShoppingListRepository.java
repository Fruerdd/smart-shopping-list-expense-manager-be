package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ShoppingListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, UUID> {
    List<ShoppingListEntity> findByOwner_UserId(UUID userId); // Find shopping lists where the user is the owner
    List<ShoppingListEntity> findByCollaborators_User_UserId(UUID userId); // Find shopping lists where the user is a collaborator
}
