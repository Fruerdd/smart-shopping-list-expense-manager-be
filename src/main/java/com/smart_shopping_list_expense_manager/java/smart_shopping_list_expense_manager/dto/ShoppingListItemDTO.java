package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListItemDTO {
    private UUID id;
    private UUID productId;
    private String productName;
    private String image;
    private Double price;
    private String storeName;
    private UUID categoryId;
    private Double quantity;
    private boolean isChecked;
    private String status;

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}