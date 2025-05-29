package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.DailySearchDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.MonthlyProductAddDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.PopularShopDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.TopProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ProductSearchLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock private ProductSearchLogRepository searchLogRepo;
    @Mock private EntityManager em;
    @Mock private Query query;
    @InjectMocks private AnalyticsService analyticsService;

    @Test
    void testGetDailySearches() {
        when(searchLogRepo.countSearchesByDay())
                .thenReturn(Collections.singletonList(new Object[]{"2025-05-27", 42L}));

        List<DailySearchDTO> result = analyticsService.getDailySearches();

        assertEquals(1, result.size());
        assertEquals("2025-05-27", result.get(0).getDay());
        assertEquals(42L,          result.get(0).getSearches());
    }

    @Test
    void testGetWeeklySearches() {
        when(searchLogRepo.countSearchesLast7Days()).thenReturn(Arrays.asList(
                new Object[]{"2025-05-22", 7L},
                new Object[]{"2025-05-23", 5L}
        ));

        List<DailySearchDTO> result = analyticsService.getWeeklySearches();

        assertEquals(2, result.size());
        assertEquals("2025-05-23", result.get(1).getDay());
        assertEquals(5L,           result.get(1).getSearches());
    }

    @Test
    void testGetMonthlyAdds() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(
                new Object[]{"2025-05", 3L},
                new Object[]{"2025-04", 0L}
        ));

        List<MonthlyProductAddDTO> dto = analyticsService.getMonthlyAdds();

        assertEquals(2, dto.size());
        assertEquals("2025-05", dto.get(0).getMonth());
        assertEquals(3L,       dto.get(0).getAddedCount());
    }

    @Test
    void testGetWeeklyProductAdds() {
        when(em.createNativeQuery(contains("interval '6 days'"))).thenReturn(query);
        when(query.getResultList()).thenReturn(
                Collections.singletonList(new Object[]{"2025-05-27", 2L})
        );

        List<DailySearchDTO> dto = analyticsService.getWeeklyProductAdds();

        assertEquals(1, dto.size());
        assertEquals("2025-05-27", dto.get(0).getDay());
        assertEquals(2L,           dto.get(0).getSearches());
    }

    @Test
    void testGetTopProductsNonEmpty() {
        when(searchLogRepo.findTopSearchedProducts(10)).thenReturn(Arrays.asList(
                new Object[]{"Apple", 10.0, 20L, "Fruit"},
                new Object[]{"Bread", 5.5,  15L, "Bakery"}
        ));

        List<TopProductDTO> list = analyticsService.getTopProducts();

        assertEquals(2, list.size());
        TopProductDTO first = list.get(0);
        assertEquals(1,       first.getRank());
        assertEquals("Apple", first.getProductName());
        assertEquals(10.0,    first.getPrice());
        assertEquals(20L,     first.getSearchCount());
        assertEquals("Fruit", first.getStoreName());
    }

    @Test
    void testGetTopProductsEmpty() {
        when(searchLogRepo.findTopSearchedProducts(10)).thenReturn(Collections.emptyList());

        List<TopProductDTO> list = analyticsService.getTopProducts();

        assertTrue(list.isEmpty());
    }

    @Test
    void testGetPopularStores() {
        when(em.createNativeQuery(contains("FROM shopping_list"))).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(
                new Object[]{"SuperMart", 8L},
                new Object[]{"QuickShop", 4L}
        ));

        List<PopularShopDTO> dto = analyticsService.getPopularStores();

        assertEquals(2, dto.size());
        assertEquals("SuperMart", dto.get(0).getName());
        assertEquals(8L,          dto.get(0).getCount());
    }
}
