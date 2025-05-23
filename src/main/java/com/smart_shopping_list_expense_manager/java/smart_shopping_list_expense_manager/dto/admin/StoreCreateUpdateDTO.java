package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreCreateUpdateDTO {
    private String name;
    private String icon;
    private String location;
    private String contact;
}
