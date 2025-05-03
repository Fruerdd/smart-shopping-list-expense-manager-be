package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import static org.hibernate.type.SqlTypes.BINARY;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopping_list")
public class ShoppingListEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "shopping_list_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", columnDefinition = "BINARY(16)")
    private UsersEntity owner;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "list_type")
    private String listType;

    @ManyToOne
    @JoinColumn(name = "store_id", columnDefinition = "BINARY(16)")
    private StoreEntity store;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    private List<CollaboratorEntity> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL)
    private List<ShoppingListItemEntity> items = new ArrayList<>();

    @Column(name = "is_active")
    private boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }
}