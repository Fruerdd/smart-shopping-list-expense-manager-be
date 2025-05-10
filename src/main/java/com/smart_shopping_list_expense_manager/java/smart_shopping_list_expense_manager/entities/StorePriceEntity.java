package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import static org.hibernate.type.SqlTypes.BINARY;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store_prices")
public class StorePriceEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "store_price_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "store_id", columnDefinition = "BINARY(16)")
    private StoreEntity store;

    @ManyToOne
    @JoinColumn(name = "product_id", columnDefinition = "BINARY(16)")
    private ProductEntity product;

    @Column(name = "price", precision = 38, scale = 2)
    private BigDecimal price;

    @Column(name = "barcode", length = 255)
    private String barcode;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Object getStorePriceId() {
        return id;
    }
}
