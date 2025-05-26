package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductDTO {
    private String storePriceId;
    private String storeId;
    private String productId;
    private String productName;
    private String category;
    private String description;
    private boolean isActive;
    private double price;
    private String barcode;
}
