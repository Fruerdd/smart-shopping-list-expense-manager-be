package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CityAllocationDTO {
    private List<String> labels;
    private List<Long> data;
}
