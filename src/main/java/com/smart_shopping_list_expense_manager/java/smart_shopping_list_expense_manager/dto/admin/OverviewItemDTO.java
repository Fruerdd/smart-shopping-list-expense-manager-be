package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverviewItemDTO {
    private String title;
    private long   value;
    private String change;
}
