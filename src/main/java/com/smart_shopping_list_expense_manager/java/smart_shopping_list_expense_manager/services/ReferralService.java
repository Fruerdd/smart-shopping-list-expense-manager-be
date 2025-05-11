package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReferralDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReferralResponse;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ReferralEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ReferralRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final UsersRepository usersRepository;

    public ReferralService(ReferralRepository referralRepository, UsersRepository usersRepository) {
        this.referralRepository = referralRepository;
        this.usersRepository = usersRepository;
    }


    public ReferralResponse createReferral(ReferralDTO dto) {
        ReferralEntity referral = new ReferralEntity();

        UsersEntity referrer = usersRepository.findById(dto.getReferrerId())
                .orElseThrow(() -> new RuntimeException("Referrer not found"));

        UsersEntity referred = usersRepository.findById(dto.getReferredUserId())
                .orElseThrow(() -> new RuntimeException("Referred user not found"));

        referral.setReferrer(referrer);
        referral.setReferredUser(referred);


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
