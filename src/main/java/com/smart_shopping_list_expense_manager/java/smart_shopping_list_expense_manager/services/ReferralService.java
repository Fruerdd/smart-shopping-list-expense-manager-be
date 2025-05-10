package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReferralDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReferralResponse;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ReferralEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ReferralRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReferralService {

    private final ReferralRepository referralRepository;

    public ReferralService(ReferralRepository referralRepository) {
        this.referralRepository = referralRepository;
    }

    public ReferralResponse createReferral(ReferralDTO dto) {
        ReferralEntity referral = new ReferralEntity();

        referral.setReferrerId(dto.getReferrerId());
        referral.setReferredUserId(dto.getReferredUserId());

        // inicijalno postavljamo statu i nagradu ako vec nisu
        referral.setStatus(dto.getStatus() != null ? dto.getStatus() : ReferralStatusEnum.PENDING);
        referral.setRewardEarned(dto.getRewardEarned() != null ? dto.getRewardEarned() : 0);

        referralRepository.save(referral);

        return new ReferralResponse("Referral invitation successfully created.");
    }

    public ReferralResponse completeReferral(UUID referredUserId) {
        ReferralEntity referral = referralRepository.findByReferredUserId(referredUserId)
                .orElseThrow(() -> new RuntimeException("Referral not found"));

        referral.setStatus(ReferralStatusEnum.PURCHASED);
        referral.setRewardEarned(50);

        referralRepository.save(referral);

        return new ReferralResponse("Referral marked as completed. Reward granted.");
    }

}
