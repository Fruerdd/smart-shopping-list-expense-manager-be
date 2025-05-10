// src/main/java/com/smart_shopping_list_expense_manager/java/smart_shopping_list_expense_manager/entities/ProductSearchLogEntity.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.time.Instant;
import java.util.UUID;

import static org.hibernate.type.SqlTypes.BINARY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_search_logs")
public class ProductSearchLogEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "search_id", columnDefinition = "BINARY(16)")
    private UUID searchId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", columnDefinition = "BINARY(16)")
    private ProductEntity product;

    @Column(name = "search_term", length = 255, nullable = false)
    private String searchTerm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)")
    private UsersEntity user;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
