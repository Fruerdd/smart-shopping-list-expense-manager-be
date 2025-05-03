package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
public class ShoppingListDTO {

    private UUID id;

    @NotNull(message = "Name cannot be null")
    private String name;

    private String description;

    @NotNull(message = "List type cannot be null")
    private String listType;

    private boolean isActive;

    @NotNull(message = "Owner ID cannot be null")
    private UUID ownerId;

    private String ownerName;

    private String ownerAvatar;

    @NotNull(message = "Store ID cannot be null")
    private UUID storeId;

    private String storeName;

    private String image;

    private String category;

    private Instant createdAt;

    private Instant updatedAt;

    @NotNull(message = "Items list cannot be null")
    private List<ShoppingListItemDTO> items = new ArrayList<>();

    @NotNull(message = "Collaborators list cannot be null")
    private List<@NotNull(message = "Collaborator cannot be null") CollaboratorDTO> collaborators = new ArrayList<>();
}