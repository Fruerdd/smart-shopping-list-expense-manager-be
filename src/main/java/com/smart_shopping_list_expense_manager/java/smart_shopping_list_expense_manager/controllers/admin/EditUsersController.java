package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.UserDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.EditUsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class EditUsersController {

    private final EditUsersService editUsersService;

    public EditUsersController(EditUsersService editUsersService) {
        this.editUsersService = editUsersService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<UsersEntity>> editMultipleUsers(
            @RequestBody @Valid List<UserDTO> usersDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Optionally, include error details in the body:
            return ResponseEntity.badRequest().build();
        }

        List<UsersEntity> savedUsers = editUsersService.editMultipleUsers(usersDto);
        return ResponseEntity.ok(savedUsers);
    }
}
