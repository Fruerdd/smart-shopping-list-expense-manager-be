package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceAverageDTO {
    private String item;
    private double avgPrice;
}