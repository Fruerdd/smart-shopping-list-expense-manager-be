// ProductSearchLogEntity.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_search_logs")
public class ProductSearchLogEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "search_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID searchId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "uuid", nullable = false)
    private ProductEntity product;

    @Column(name = "search_term", length = 255, nullable = false)
    private String searchTerm;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UsersEntity user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
