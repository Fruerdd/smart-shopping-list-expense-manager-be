package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CategoryDTO {
    private UUID id;
    private String name;
    private String icon;
    private List<ShoppingListItemDTO> products;
}
