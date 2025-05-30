package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneySpentDTO {
    private String month;
    private double thisYear;
    private double lastYear;
}
