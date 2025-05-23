package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.AddProductDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.BulkResultDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> addMultipleProducts(
            @RequestBody @Valid List<AddProductDTO> dtoList,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            // collect all error messages into a JSON-friendly list
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity
                    .badRequest()
                    .body(new BulkResultDTO(false, errors, 0));
        }

        try {
            int count = productService.addProducts(dtoList).size();
            return ResponseEntity.ok(new BulkResultDTO(true, null, count));
        } catch (Exception ex) {
            // log the exception on the server
            ex.printStackTrace();
            // return a JSON error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BulkResultDTO(false,
                            List.of("Update failed: " + ex.getMessage()),
                            0));
        }
    }
}
