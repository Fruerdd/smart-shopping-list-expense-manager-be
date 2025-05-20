package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store_prices")
public class StorePriceEntity {

    // 1) Rename the field to match the getter you’re calling:
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "store_price_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID storePriceId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", columnDefinition = "uuid", nullable = false)
    private StoreEntity store;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "uuid", nullable = false)
    private ProductEntity product;

    @Column(name = "price", precision = 38, scale = 2)
    private BigDecimal price;

    @Column(name = "barcode", length = 255)
    private String barcode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
