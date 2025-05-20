// ReferralEntity.java
package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferralEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", referencedColumnName = "user_id", columnDefinition = "uuid", nullable = false)
    private UsersEntity referrer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_user_id", referencedColumnName = "user_id", columnDefinition = "uuid", nullable = false)
    private UsersEntity referredUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReferralStatusEnum status;

    @Column(name = "reward_earned")
    private Integer rewardEarned;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
