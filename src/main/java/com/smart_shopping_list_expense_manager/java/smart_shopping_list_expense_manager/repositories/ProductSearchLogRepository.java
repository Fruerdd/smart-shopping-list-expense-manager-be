package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ProductSearchLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;  // ← add this import

@Repository
public interface ProductSearchLogRepository extends JpaRepository<ProductSearchLogEntity, UUID> {

    /** existing: counts all-time searches by day */
    @Query("""
      SELECT FUNCTION('DATE', p.createdAt), COUNT(p)
      FROM ProductSearchLogEntity p
      GROUP BY FUNCTION('DATE', p.createdAt)
      ORDER BY FUNCTION('DATE', p.createdAt)
    """)
    List<Object[]> countSearchesByDay();

    /** counts only those in the last 7 days */
    @Query(value =
            "SELECT DATE(created_at), COUNT(*) " +
                    "  FROM product_search_logs " +
                    " WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                    " GROUP BY DATE(created_at) " +
                    " ORDER BY DATE(created_at)",
            nativeQuery = true
    )
    List<Object[]> countSearchesLast7Days();
}
