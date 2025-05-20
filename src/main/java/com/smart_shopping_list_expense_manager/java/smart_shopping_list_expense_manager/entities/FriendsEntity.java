// FriendsEntity.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "friends")
public class FriendsEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "friend_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UsersEntity user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id_2", columnDefinition = "uuid", nullable = false)
    private UsersEntity friend;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
