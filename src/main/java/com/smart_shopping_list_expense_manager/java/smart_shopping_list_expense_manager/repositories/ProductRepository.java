package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    Optional<ProductEntity> findByName(String name);
    @Query(value =
            "SELECT DATE(p.created_at) AS day, COUNT(*) AS cnt " +
                    "FROM products p " +
                    "WHERE p.created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    "GROUP BY DATE(p.created_at) " +
                    "ORDER BY DATE(p.created_at)",
            nativeQuery = true
    )
    List<Object[]> countAddsLast7Days();
}
