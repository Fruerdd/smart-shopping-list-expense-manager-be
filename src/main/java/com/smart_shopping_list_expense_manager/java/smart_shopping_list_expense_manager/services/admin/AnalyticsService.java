// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/services/admin/AnalyticsService.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.DailySearchDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.MonthlyProductAddDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.PopularShopDTO;
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
                "WITH months AS (\n" +
                        "  SELECT to_char(current_date - (interval '1 month' * s), 'YYYY-MM') AS month\n" +
                        "  FROM generate_series(0,5) AS s\n" +
                        "),\n" +
                        "prod_counts AS (\n" +
                        "  SELECT to_char(created_at, 'YYYY-MM')       AS month,\n" +
                        "         count(*)                             AS added_count\n" +
                        "  FROM products\n" +
                        "  GROUP BY to_char(created_at, 'YYYY-MM')\n" +
                        ")\n" +
                        "SELECT m.month, COALESCE(pc.added_count, 0)\n" +
                        "FROM months m\n" +
                        "LEFT JOIN prod_counts pc ON pc.month = m.month\n" +
                        "ORDER BY m.month;"
        ).getResultList();

        return rows.stream()
                .map(r -> new MonthlyProductAddDTO(
                        (String) r[0],
                        ((Number) r[1]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public List<DailySearchDTO> getWeeklyProductAdds() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                "SELECT to_char(created_at::date, 'YYYY-MM-DD') AS day,\n" +
                        "       count(*)                              AS added_count\n" +
                        "FROM products\n" +
                        "WHERE created_at >= current_date - interval '6 days'\n" +
                        "GROUP BY created_at::date\n" +
                        "ORDER BY created_at::date;"
        ).getResultList();

        return rows.stream()
                .map(r -> new DailySearchDTO(
                        (String) r[0],
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

    /**
     * @return list of (store name, total appearances in shopping_list)
     */
    public List<PopularShopDTO> getPopularStores() {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
                "SELECT s.name, COUNT(*) AS cnt " +
                        "FROM shopping_list sl " +
                        "JOIN stores s ON sl.store_id = s.store_id " +
                        "GROUP BY s.name " +
                        "ORDER BY cnt DESC"
        ).getResultList();

        return rows.stream()
                .map(r -> new PopularShopDTO(
                        (String) r[0],
                        ((Number) r[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
}
