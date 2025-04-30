package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.UUID;

import static org.hibernate.type.SqlTypes.BINARY;

@Data
@Entity
@Table(name = "favorite_stores")
public class FavoriteStoreEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "favorite_store_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private UsersEntity user;

    @ManyToOne
    @JoinColumn(name = "store_id", columnDefinition = "BINARY(16)")
    private StoreEntity store;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
