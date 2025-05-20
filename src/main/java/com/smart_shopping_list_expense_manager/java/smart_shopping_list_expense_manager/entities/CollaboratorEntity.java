// CollaboratorEntity.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.PermissionEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "collaborators",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_shopping_list_user",
                columnNames = {"shopping_list_id", "user_id"}
        )
)
public class CollaboratorEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "collab_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", columnDefinition = "uuid", nullable = false)
    private ShoppingListEntity shoppingList;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UsersEntity user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", length = 50, nullable = false)
    private PermissionEnum permission;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
