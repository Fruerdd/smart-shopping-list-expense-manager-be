package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.LoyaltyDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.LoyaltyEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.LoyaltyRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LoyaltyService {

    private final LoyaltyRepository loyaltyRepository;
    private final UsersRepository usersRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository, UsersRepository usersRepository) {
        this.loyaltyRepository = loyaltyRepository;
        this.usersRepository = usersRepository;
    }

    public LoyaltyDTO getLoyaltyStatus(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<LoyaltyEntity> loyaltyOpt = loyaltyRepository.findByUser(user);

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
