package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FavoriteProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProductEntity, UUID> {
    @Query("SELECT fp FROM FavoriteProductEntity fp WHERE fp.user.userId = :userId AND fp.product.productId = :productId")
    Optional<FavoriteProductEntity> findByUserIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);
}