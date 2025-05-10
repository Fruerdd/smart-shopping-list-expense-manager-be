package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AddProductDTO {
    // when editing an existing price record
    private UUID storePriceId;

    @NotNull
    private UUID storeId;

    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String category;    // now a simple string

    private String description;

    private boolean isActive = true;

    @NotNull
    private BigDecimal price;

    private String barcode;
}
