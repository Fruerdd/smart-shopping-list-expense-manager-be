package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.CollaboratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CollaboratorRepository extends JpaRepository<CollaboratorEntity, UUID> {
    Optional<CollaboratorEntity> findByShoppingList_IdAndUser_UserId(UUID shoppingListId, UUID userId); // Find collaborator by shopping list ID and user ID
}
