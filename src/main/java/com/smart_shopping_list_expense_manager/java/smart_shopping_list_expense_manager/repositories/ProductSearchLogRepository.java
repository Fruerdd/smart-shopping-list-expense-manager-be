package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductSearchLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductSearchLogRepository extends JpaRepository<ProductSearchLogEntity, UUID> {

    @Query("SELECT COUNT(p) FROM ProductSearchLogEntity p WHERE p.createdAt >= :start AND p.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") Instant start,
                                 @Param("end")   Instant end);

    @Query(value = """
        SELECT to_char(created_at::date, 'YYYY-MM-DD') AS day,
               COUNT(*)                                  AS cnt
          FROM product_search_logs
         GROUP BY created_at::date
         ORDER BY created_at::date
      """, nativeQuery = true)
    List<Object[]> countSearchesByDay();

    @Query(value = """
        SELECT to_char(created_at::date, 'YYYY-MM-DD') AS day,
               COUNT(*)                                  AS cnt
          FROM product_search_logs
         WHERE created_at >= current_date - interval '6 days'
         GROUP BY created_at::date
         ORDER BY created_at::date
      """, nativeQuery = true)
    List<Object[]> countSearchesLast7Days();
    @Query(value = """
        SELECT p.name                   AS productName,
               sp.price                 AS price,
               COUNT(l.search_id)       AS cnt,
               s.name                   AS storeName
          FROM product_search_logs l
          JOIN products p ON l.product_id = p.product_id
          JOIN store_prices sp ON sp.product_id = p.product_id
          JOIN stores s ON sp.store_id = s.store_id
         GROUP BY p.name, sp.price, s.name
         ORDER BY cnt DESC
         LIMIT :limit
      """, nativeQuery = true)
    List<Object[]> findTopSearchedProducts(@Param("limit") int limit);
}
