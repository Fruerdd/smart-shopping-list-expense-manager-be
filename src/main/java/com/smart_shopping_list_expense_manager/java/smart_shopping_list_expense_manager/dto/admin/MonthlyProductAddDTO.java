package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MonthlyProductAddDTO {
    private String month;
    private Long   addedCount;
}
