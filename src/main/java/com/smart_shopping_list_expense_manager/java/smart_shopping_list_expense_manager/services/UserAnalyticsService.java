package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user.*;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAnalyticsService {
    @PersistenceContext
    private EntityManager em;

    public List<MoneySpentDTO> calculateMoneySpent(UUID userId) {
        int curr = LocalDate.now().getYear();
        int last = curr - 1;

        String sql = """
            SELECT
              to_char(sl.created_at,'Mon')                                  AS month,
              COALESCE(SUM(CASE WHEN date_part('year',sl.created_at)=?1
                                THEN sli.quantity * sp.price ELSE 0 END),0) AS thisYear,
              COALESCE(SUM(CASE WHEN date_part('year',sl.created_at)=?2
                                THEN sli.quantity * sp.price ELSE 0 END),0) AS lastYear
            FROM shopping_list sl
            JOIN shopping_list_items sli
              ON sl.shopping_list_id = sli.shopping_list_id
            JOIN store_prices sp
              ON sli.product_id = sp.product_id
             AND sl.store_id      = sp.store_id
            WHERE sl.owner_id = ?3
            GROUP BY date_part('month', sl.created_at), to_char(sl.created_at,'Mon')
            ORDER BY date_part('month', sl.created_at)
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, curr)
                .setParameter(2, last)
                .setParameter(3, userId)
                .getResultList();

        return rows.stream()
                .map(r -> new MoneySpentDTO(
                        (String)  r[0],
                        ((Number) r[1]).doubleValue(),
                        ((Number) r[2]).doubleValue()))
                .collect(Collectors.toList());
    }

    public List<PriceAverageDTO> calculatePriceAverages(UUID userId) {
        String sql = """
            SELECT p.name      AS item,
                   COALESCE(AVG(sp.price),0) AS avgPrice
            FROM shopping_list sl
            JOIN shopping_list_items sli
              ON sl.shopping_list_id = sli.shopping_list_id
            JOIN store_prices sp
              ON sli.product_id = sp.product_id
             AND sl.store_id      = sp.store_id
            JOIN products p
              ON p.product_id = sli.product_id
            WHERE sl.owner_id = ?1
            GROUP BY p.name
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .getResultList();

        return rows.stream()
                .map(r -> new PriceAverageDTO(
                        (String)  r[0],
                        ((Number) r[1]).doubleValue()))
                .collect(Collectors.toList());
    }

    public List<StoreExpenseDTO> calculateStoreExpenses(UUID userId) {
        String sql = """
        WITH total_spent AS (
          SELECT
            COALESCE(SUM(sli.quantity * sp.price), 0) AS grand
          FROM shopping_list sl
          JOIN shopping_list_items sli
            ON sl.shopping_list_id = sli.shopping_list_id
          JOIN store_prices sp
            ON sli.product_id = sp.product_id
           AND sl.store_id      = sp.store_id
          WHERE sl.owner_id = ?1
        )
        SELECT
          s.name AS store,
          ROUND(
            100.0 * COALESCE(SUM(sli.quantity * sp.price), 0)
            / NULLIF(ts.grand, 0),
            2
          ) AS pct
        FROM shopping_list sl
        JOIN shopping_list_items sli
          ON sl.shopping_list_id = sli.shopping_list_id
        JOIN store_prices sp
          ON sli.product_id = sp.product_id
         AND sl.store_id      = sp.store_id
        JOIN stores s
          ON s.store_id = sl.store_id
        CROSS JOIN total_spent ts
        WHERE sl.owner_id = ?1
        GROUP BY s.name, ts.grand
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .getResultList();

        return rows.stream()
                .map(r -> new StoreExpenseDTO(
                        (String)  r[0],
                        ((Number) r[1]).doubleValue()))
                .collect(Collectors.toList());
    }


    public List<SavingDTO> calculateSavings(UUID userId) {
        int curr = LocalDate.now().getYear();
        int last = curr - 1;

        String sql = """
            SELECT
              to_char(sl.created_at,'Mon')                                 AS month,
              GREATEST(
                COALESCE(SUM(CASE WHEN date_part('year',sl.created_at)=?2
                                  THEN sli.quantity * sp.price ELSE 0 END),0)
              - COALESCE(SUM(CASE WHEN date_part('year',sl.created_at)=?1
                                  THEN sli.quantity * sp.price ELSE 0 END),0),
              0)                                                          AS amount
            FROM shopping_list sl
            JOIN shopping_list_items sli
              ON sl.shopping_list_id = sli.shopping_list_id
            JOIN store_prices sp
              ON sli.product_id = sp.product_id
             AND sl.store_id      = sp.store_id
            WHERE sl.owner_id = ?3
            GROUP BY date_part('month', sl.created_at), to_char(sl.created_at,'Mon')
            ORDER BY date_part('month', sl.created_at)
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, curr)
                .setParameter(2, last)
                .setParameter(3, userId)
                .getResultList();

        return rows.stream()
                .map(r -> new SavingDTO(
                        (String)  r[0],
                        ((Number) r[1]).doubleValue()))
                .collect(Collectors.toList());
    }

    public List<CategorySpendDTO> calculateCategorySpending(UUID userId) {
        String sql = """
            SELECT c.name                           AS category,
                   COALESCE(SUM(sli.quantity * sp.price),0) AS spent
            FROM shopping_list sl
            JOIN shopping_list_items sli
              ON sl.shopping_list_id = sli.shopping_list_id
            JOIN store_prices sp
              ON sli.product_id = sp.product_id
             AND sl.store_id      = sp.store_id
            JOIN products p
              ON p.product_id = sli.product_id
            JOIN categories c
              ON c.category_id = p.category_id
            WHERE sl.owner_id = ?1
            GROUP BY c.name
        """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .getResultList();

        return rows.stream()
                .map(r -> new CategorySpendDTO(
                        (String)  r[0],
                        ((Number) r[1]).doubleValue()))
                .collect(Collectors.toList());
    }
}