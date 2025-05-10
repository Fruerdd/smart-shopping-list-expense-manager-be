package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LoyaltyDTO {
    private UUID userId;
    private Integer points;
    private LoyaltyTierEnum tier;
}
