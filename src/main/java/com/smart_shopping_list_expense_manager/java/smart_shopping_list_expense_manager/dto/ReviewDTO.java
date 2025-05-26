package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private String name;
    private Double reviewScore;
    private String reviewContext;
    private String avatar;
}
