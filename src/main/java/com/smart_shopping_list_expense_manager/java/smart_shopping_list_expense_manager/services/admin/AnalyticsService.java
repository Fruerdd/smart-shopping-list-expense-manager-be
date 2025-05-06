// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/services/admin/AnalyticsService.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.DailySearchDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.MonthlyProductAddDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductSearchLogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

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
                "SELECT DATE_FORMAT(created_at,'%Y-%m'), COUNT(*) " +
                        "FROM products GROUP BY 1 ORDER BY 1"
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
}
