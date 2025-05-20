package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;
    private String email;
    private String name;
    private LoyaltyTierEnum loyaltyTier;
    private Integer bonus_points;
    private List<ShoppingListDTO> shoppingLists;
}