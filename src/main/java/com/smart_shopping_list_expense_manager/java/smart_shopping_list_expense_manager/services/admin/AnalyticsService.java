// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/services/admin/AnalyticsService.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.DailySearchDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.MonthlyProductAddDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.TopProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductSearchLogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final ProductSearchLogRepository searchLogRepo;
    private final EntityManager             em;

    public AnalyticsService(ProductSearchLogRepository searchLogRepo,
                            EntityManager em) {
        this.searchLogRepo = searchLogRepo;
        this.em            = em;
    }

    public List<DailySearchDTO> getDailySearches() {
        return searchLogRepo.countSearchesByDay().stream()
                .map(arr -> new DailySearchDTO(
                        arr[0].toString(),
                        ((Number)arr[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public List<MonthlyProductAddDTO> getMonthlyAdds() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                // 1) build a 6-row set of months: current and 5 before
                "WITH months AS (\n" +
                        "  SELECT DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL seq MONTH), '%Y-%m') AS month\n" +
                        "  FROM (SELECT 0 AS seq UNION ALL SELECT 1 UNION ALL SELECT 2\n" +
                        "        UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) AS m\n" +
                        "),\n" +
                        // 2) count real products by month
                        "prod_counts AS (\n" +
                        "  SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS cnt\n" +
                        "  FROM products\n" +
                        "  GROUP BY 1\n" +
                        ")\n" +
                        // 3) left-join so every month appears
                        "SELECT months.month, COALESCE(prod_counts.cnt, 0) AS addedCount\n" +
                        "FROM months\n" +
                        "LEFT JOIN prod_counts ON prod_counts.month = months.month\n" +
                        "ORDER BY months.month;"
        ).getResultList();

        return rows.stream()
                .map(r -> new MonthlyProductAddDTO(
                        (String)r[0],
                        ((Number)r[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Returns one DTO per of the last 7 days, each containing:
     *  - day   = DATE(created_at)
     *  - searches = count of products created that day
     */
    public List<DailySearchDTO> getWeeklyProductAdds() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                "SELECT DATE(created_at), COUNT(*) " +
                        "  FROM products " +
                        " WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                        " GROUP BY DATE(created_at) " +
                        " ORDER BY DATE(created_at)"
        ).getResultList();

        return rows.stream()
                .map(r -> new DailySearchDTO(
                        r[0].toString(),
                        ((Number) r[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public List<DailySearchDTO> getWeeklySearches() {
        return searchLogRepo.countSearchesLast7Days()
                .stream()
                .map(arr -> new DailySearchDTO(
                        arr[0].toString(),                         // "YYYY-MM-DD"
                        ((Number) arr[1]).longValue()              // count
                ))
                .collect(Collectors.toList());
    }

    public List<TopProductDTO> getTopProducts() {
        List<Object[]> rows = searchLogRepo.findTopSearchedProducts(10);
        List<TopProductDTO> list = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Object[] r = rows.get(i);
            list.add(new TopProductDTO(
                    i + 1,
                    (String) r[0],
                    ((Number) r[1]).doubleValue(),
                    ((Number) r[2]).longValue(),
                    (String) r[3]
            ));
        }
        return list;
    }
}
