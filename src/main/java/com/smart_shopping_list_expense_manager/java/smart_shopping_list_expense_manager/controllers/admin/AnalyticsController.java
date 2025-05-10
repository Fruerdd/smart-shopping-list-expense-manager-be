// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/controllers/admin/AnalyticsController.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.DailySearchDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.MonthlyProductAddDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.TopProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/analytics")
@CrossOrigin("http://localhost:4200")
public class AnalyticsController {
    private final AnalyticsService svc;
    public AnalyticsController(AnalyticsService svc) { this.svc = svc; }

    @GetMapping("/daily-searches")
    public ResponseEntity<List<DailySearchDTO>> dailySearches() {
        return ResponseEntity.ok(svc.getDailySearches());
    }

    @GetMapping("/weekly-adds")
    public ResponseEntity<List<DailySearchDTO>> weeklyAdds() {
        return ResponseEntity.ok(svc.getWeeklyProductAdds());
    }

    @GetMapping("/monthly-adds")
    public ResponseEntity<List<MonthlyProductAddDTO>> monthlyAdds() {
        return ResponseEntity.ok(svc.getMonthlyAdds());
    }

    @GetMapping("/weekly-searches")
    public ResponseEntity<List<DailySearchDTO>> weeklySearches() {
        return ResponseEntity.ok(svc.getWeeklySearches());
    }

    @GetMapping("/top")
    public ResponseEntity<List<TopProductDTO>> topProducts() {
        return ResponseEntity.ok(svc.getTopProducts());
    }


}
