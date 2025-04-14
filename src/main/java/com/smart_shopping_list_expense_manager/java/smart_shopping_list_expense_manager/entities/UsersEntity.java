package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities;

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
@Table(name = "users")
public class UsersEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(BINARY)
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "referral_code", length = 50)
    private String referralCode;

    @Column(name = "promo_code", length = 50)
    private String promoCode;

    @Column(name = "bonus_points")
    private Integer bonusPoints;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "location")
    private String location;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "review_score")
    private Double reviewScore;

    @Column(name = "review_context")
    private String reviewContext;


    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
