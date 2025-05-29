package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.CollaboratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CollaboratorRepository extends JpaRepository<CollaboratorEntity, UUID> {
    Optional<CollaboratorEntity> findByShoppingList_IdAndUser_UserId(UUID shoppingListId, UUID userId); // Find collaborator by shopping list ID and user ID

    @Modifying
    @Query("DELETE FROM CollaboratorEntity c WHERE c.shoppingList.id = :listId AND c.user.id NOT IN :userIds")
    void deleteByShoppingListIdAndUserIdNotIn(UUID listId, Set<UUID> userIds);

}
