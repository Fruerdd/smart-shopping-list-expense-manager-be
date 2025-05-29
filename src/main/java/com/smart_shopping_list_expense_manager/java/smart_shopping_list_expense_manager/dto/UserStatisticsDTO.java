package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private UUID userId;
    private int totalLists;
    private int totalProducts;
    private int totalItems;
    private int totalStores;
    private int totalFavoriteStores;
    private int totalFavoriteProducts;
    private double totalSpent;
    private double averageSpentPerList;
    private String mostFrequentStore;
    private String mostBoughtItem;
    private int totalCategories;
    private int totalCollaborators;
    private String loyaltyTier;

}
