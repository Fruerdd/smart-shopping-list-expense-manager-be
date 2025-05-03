package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.PermissionEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import static org.hibernate.type.SqlTypes.BINARY;
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
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "collab_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id", columnDefinition = "BINARY(16)")
    private ShoppingListEntity shoppingList;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private UsersEntity user;

    @Column(name = "created_at")
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", length = 50, nullable = false)
    private PermissionEnum permission;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}