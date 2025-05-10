// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/dto/analytics/MonthlyProductAddDTO.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.*;

/**
 * Count of new products added per month.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MonthlyProductAddDTO {
    /** e.g. "2024-10" */
    private String month;
    /** number of products created that month */
    private Long   addedCount;
}
