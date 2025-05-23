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
    private final UsersRepository repo;

    public EditUsersService(UsersRepository repo) {
        this.repo = repo;
    }

    private UserDTO toDto(UsersEntity e) {
        UserDTO dto = new UserDTO();
        dto.setId(e.getUserId());
        dto.setName(e.getName());
        dto.setEmail(e.getEmail());
        dto.setPhoneNumber(e.getPhoneNumber());
        dto.setReferralCode(e.getReferralCode());
        dto.setPromoCode(e.getPromoCode());
        dto.setBonusPoints(e.getBonusPoints());
        dto.setDeviceInfo(e.getDeviceInfo());
        dto.setLocation(e.getLocation());
        dto.setUserType(e.getUserType());           // <-- role
        dto.setIsActive(e.getIsActive());
        dto.setReviewScore(e.getReviewScore());
        dto.setReviewContext(e.getReviewContext());
        return dto;
    }
    private UsersEntity toEntity(UserDTO dto) {
        UsersEntity e = dto.getId() != null
                ? repo.findById(dto.getId()).orElse(new UsersEntity())
                : new UsersEntity();
        e.setName(dto.getName());
        e.setEmail(dto.getEmail());
        e.setPassword(dto.getPassword());
        e.setPhoneNumber(dto.getPhoneNumber());
        e.setReferralCode(dto.getReferralCode());
        e.setPromoCode(dto.getPromoCode());
        e.setBonusPoints(dto.getBonusPoints());
        e.setDeviceInfo(dto.getDeviceInfo());
        e.setLocation(dto.getLocation());
        e.setUserType(dto.getUserType());            // <-- role
        e.setIsActive(dto.getIsActive());
        e.setReviewScore(dto.getReviewScore());
        e.setReviewContext(dto.getReviewContext());
        return e;
    }

    public List<UserDTO> getAllUsers() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserDTO> editMultipleUsers(List<UserDTO> dtos) {
        List<UsersEntity> saved = dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        saved = repo.saveAll(saved);
        return saved.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
