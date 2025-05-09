package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers.admin;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin.UserDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.admin.EditUsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("http://localhost:4200")
public class EditUsersController {
    private final EditUsersService svc;
    public EditUsersController(EditUsersService svc) {
        this.svc = svc;
    }

    /** GET /api/users → all users */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(svc.getAllUsers());
    }

    /** PUT /api/users/bulk → bulk update */
    @PutMapping("/bulk")
    public ResponseEntity<List<UserDTO>> bulkUpdate(
            @RequestBody @Valid List<UserDTO> usersDto,
            BindingResult br
    ) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(svc.editMultipleUsers(usersDto));
    }
}
