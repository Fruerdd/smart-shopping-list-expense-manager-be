package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReferralDTO {
    private UUID referrerId;
    private UUID referredUserId;
    private ReferralStatusEnum status;     // Enum: PENDING, REGISTERED, PURCHASED
    private Integer rewardEarned;
}
