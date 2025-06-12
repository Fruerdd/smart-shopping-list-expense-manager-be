package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddProductDTO {
    @NotNull
    private UUID storeId;

    private UUID productId;

    @NotBlank
    private String productName;

    private UUID categoryId;
    private String categoryName;

    private String description;

    @NotNull
    private BigDecimal price;

    private String barcode;

    @NotNull
    private Boolean isActive;
}
