package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkResultDTO {
    private boolean       success;
    private List<String>  errors;
    private int           count;
}
