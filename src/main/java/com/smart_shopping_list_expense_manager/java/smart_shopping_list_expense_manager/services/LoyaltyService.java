package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.LoyaltyDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.LoyaltyEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.LoyaltyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LoyaltyService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    public LoyaltyDTO getLoyaltyStatus(UUID userId) {
        Optional<LoyaltyEntity> loyaltyOpt = loyaltyRepository.findByUserId(userId);

        LoyaltyDTO dto = new LoyaltyDTO();
        dto.setUserId(userId);

        if (loyaltyOpt.isPresent()) {
            LoyaltyEntity entity = loyaltyOpt.get();
            dto.setPoints(entity.getPoints());
            dto.setTier(calculateTier(entity.getPoints()));
        } else {
            dto.setPoints(0);
            dto.setTier(LoyaltyTierEnum.BRONZE);
        }

        return dto;
    }

    private LoyaltyTierEnum calculateTier(int points) {
        if (points >= 500) return LoyaltyTierEnum.GOLD;
        if (points >= 100) return LoyaltyTierEnum.SILVER;
        return LoyaltyTierEnum.BRONZE;
    }
}
