package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.CustomerDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class UsersController {

    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public List<CustomerDTO> getAllCustomers() {
        List<UsersEntity> users = usersRepository.findAll();

        return users.stream()
                .map(user -> new CustomerDTO(
                        user.getName(),
                        user.getPhoneNumber(),
                        user.getEmail(),
                        user.getLocation(),
                        (user.getIsActive() != null && user.getIsActive()) ? "Active" : "Inactive"
                ))
                .collect(Collectors.toList());
    }
}
