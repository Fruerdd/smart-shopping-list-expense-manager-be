package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.CityAllocationDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.CityAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class CityAllocationController {

    private final CityAllocationService service;

    public CityAllocationController(CityAllocationService service) {
        this.service = service;
    }

    @GetMapping("/city-allocation")
    public ResponseEntity<CityAllocationDTO> getCityAllocation() {
        CityAllocationDTO dto = service.getCityAllocation();
        return ResponseEntity.ok(dto);
    }
}
