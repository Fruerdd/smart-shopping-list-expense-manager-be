// ShoppingListEntity.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shopping_list")
public class ShoppingListEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "shopping_list_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", columnDefinition = "uuid", nullable = false)
    private UsersEntity owner;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "list_type")
    private String listType;

    @ManyToOne
    @JoinColumn(name = "store_id", columnDefinition = "uuid")
    private StoreEntity store;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollaboratorEntity> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingListItemEntity> items = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        if (updatedAt == null) updatedAt = Instant.now();
    }
}
