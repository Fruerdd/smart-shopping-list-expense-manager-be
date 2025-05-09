package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TopProductDTO {
    private int    rank;
    private String productName;
    private double price;
    private long   searchCount;
    private String storeName;
}
