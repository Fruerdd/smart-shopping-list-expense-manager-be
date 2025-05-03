package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.CategoryEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductDTO {
    private UUID id;
    private String name;
    private CategoryEntity category;
}
