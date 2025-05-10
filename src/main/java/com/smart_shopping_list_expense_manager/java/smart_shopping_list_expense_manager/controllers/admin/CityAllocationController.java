package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.CityAllocationDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.CityAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin("http://localhost:4200")
public class CityAllocationController {

    private final CityAllocationService service;

    public CityAllocationController(CityAllocationService service) {
        this.service = service;
    }

    /**
     * Matches Angular’s GET http://localhost:8080/api/stats/city-allocation
     */
    @GetMapping("/city-allocation")
    public ResponseEntity<CityAllocationDTO> getCityAllocation() {
        CityAllocationDTO dto = service.getCityAllocation();
        return ResponseEntity.ok(dto);
    }
}
