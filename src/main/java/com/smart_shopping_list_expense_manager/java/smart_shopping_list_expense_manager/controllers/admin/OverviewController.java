package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;


import org.springframework.web.bind.annotation.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.OverviewItemDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.OverviewService;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin("http://localhost:4200")
public class OverviewController {
    private final OverviewService service;
    public OverviewController(OverviewService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public List<OverviewItemDTO> getOverview(@RequestParam(defaultValue="today") String period) {
        return service.getOverview(period);
    }
}
