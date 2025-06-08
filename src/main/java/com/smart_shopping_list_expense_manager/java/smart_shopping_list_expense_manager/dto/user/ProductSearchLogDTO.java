package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.user;


import lombok.Data;

import java.util.UUID;

@Data
public class ProductSearchLogDTO {
    private UUID productId;
    private String searchTerm;
}

