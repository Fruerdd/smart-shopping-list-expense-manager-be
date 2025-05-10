package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.UUID;

import static org.hibernate.type.SqlTypes.BINARY;

@Entity
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferralEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @JdbcTypeCode(BINARY)
    @Column(name = "referrer_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID referrerId;

    @JdbcTypeCode(BINARY)
    @Column(name = "referred_user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID referredUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReferralStatusEnum status;

    @Column(name = "reward_earned")
    private Integer rewardEarned;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
