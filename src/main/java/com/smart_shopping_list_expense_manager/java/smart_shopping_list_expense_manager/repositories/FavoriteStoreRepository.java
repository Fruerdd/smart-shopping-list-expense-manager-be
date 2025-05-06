package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FavoriteStoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteStoreRepository extends JpaRepository<FavoriteStoreEntity, UUID> {
    @Query("SELECT fs FROM FavoriteStoreEntity fs WHERE fs.user.userId = :userId AND fs.store.storeId = :storeId")
    Optional<FavoriteStoreEntity> findByUserIdAndStoreId(@Param("userId") UUID userId, @Param("storeId") UUID storeId);
}