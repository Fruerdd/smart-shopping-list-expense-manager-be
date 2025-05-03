package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreItemDTO {
    private UUID storeId;
    private String storeName;
    private String storeIcon;
    private Double price;
}
