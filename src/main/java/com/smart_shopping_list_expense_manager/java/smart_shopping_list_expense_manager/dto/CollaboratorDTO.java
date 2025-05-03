package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.PermissionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CollaboratorDTO {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    private String userName;

    @NotNull(message = "Permission cannot be null")
    private String permission;

    public void setPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("Permission cannot be null");
        }
        try {
            PermissionEnum.valueOf(permission.toUpperCase());
            this.permission = permission.toUpperCase();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid permission value: " + permission + ". Must be one of: VIEW, EDIT");
        }
    }
}