package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.UserDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditUsersService {

    private final UsersRepository userRepository;

    public EditUsersService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Converts a single DTO to an Entity.
    private UsersEntity mapDtoToEntity(UserDTO dto) {
        UsersEntity entity = new UsersEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setReferralCode(dto.getReferralCode());
        entity.setPromoCode(dto.getPromoCode());
        entity.setBonusPoints(dto.getBonusPoints());
        entity.setDeviceInfo(dto.getDeviceInfo());
        entity.setLocation(dto.getLocation());
        entity.setUserType(dto.getUserType());
        entity.setIsActive(dto.getIsActive());
        entity.setReviewScore(dto.getReviewScore());
        entity.setReviewContext(dto.getReviewContext());
        return entity;
    }

    // Business logic for bulk editing or adding users.
    @Transactional
    public List<UsersEntity> editMultipleUsers(List<UserDTO> usersDto) {
        List<UsersEntity> entities = usersDto.stream()
                .map(this::mapDtoToEntity)
                .collect(Collectors.toList());
        return userRepository.saveAll(entities);
    }
}
