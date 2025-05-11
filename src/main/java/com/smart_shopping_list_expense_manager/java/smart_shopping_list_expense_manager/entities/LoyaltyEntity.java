package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.LoyaltyTierEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.UUID;

import static org.hibernate.type.SqlTypes.BINARY;

@Entity
@Table(name = "loyalty")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UsersEntity user;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private LoyaltyTierEnum tier;
}
