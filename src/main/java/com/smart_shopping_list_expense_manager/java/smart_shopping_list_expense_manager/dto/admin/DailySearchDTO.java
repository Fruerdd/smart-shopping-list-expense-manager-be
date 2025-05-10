// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/dto/analytics/DailySearchDTO.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DailySearchDTO {
    /** e.g. "2024-10-06" */
    private String day;
    /** total searches on that day */
    private Long   searches;
}
