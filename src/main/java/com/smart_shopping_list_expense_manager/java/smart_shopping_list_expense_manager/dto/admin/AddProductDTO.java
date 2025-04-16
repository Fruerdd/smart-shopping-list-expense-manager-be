package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddProductDTO {

    // If a user searches or picks an existing store, we pass the storeId
    @NotNull
    private UUID storeId;

    // If product exists, we can pass productId, else name/category create new
    private UUID productId;

    @NotBlank
    private String productName;

    private String category;
    private String description;
    private boolean isActive = true;

    // Price details for this store
    @NotNull
    private BigDecimal price;

    private String barcode;
}

