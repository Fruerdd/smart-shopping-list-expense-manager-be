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

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkResultDTO> addMultipleProducts(
            @Valid @RequestBody List<AddProductDTO> dtoList,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> validationErrors = bindingResult.getFieldErrors().stream()
                    .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                    .collect(Collectors.toList());
            BulkResultDTO result = new BulkResultDTO(false, validationErrors, 0);
            return ResponseEntity.badRequest().body(result);
        }

        try {
            BulkResultDTO result = productService.addProducts(dtoList);

            HttpStatus status = result.isSuccess() ? HttpStatus.OK : HttpStatus.MULTI_STATUS;
            return new ResponseEntity<>(result, status);
        } catch (Exception ex) {
            ex.printStackTrace();
            BulkResultDTO errorResult = new BulkResultDTO(
                    false,
                    List.of("Internal error: " + ex.getMessage()),
                    0
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}
