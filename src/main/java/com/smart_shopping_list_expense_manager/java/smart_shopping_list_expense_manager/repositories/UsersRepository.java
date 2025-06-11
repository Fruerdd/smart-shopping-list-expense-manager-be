package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, UUID> {
    Optional<UsersEntity> findByEmail(String email);

    Optional<UsersEntity> findByReferralCode(String referralCode);

    Optional<UsersEntity> findByPromoCode(String promoCode);

    List<UsersEntity> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    List<UsersEntity> findByReviewScoreIsNotNullAndReviewContextIsNotNull();

    @Query("SELECT u.location, COUNT(u) FROM UsersEntity u GROUP BY u.location")
    List<Object[]> countByCity();

    @Query("SELECT COUNT(u) FROM UsersEntity u WHERE u.createdAt >= :start AND u.createdAt < :end")
    long countNewUsersBetween(@Param("start") Instant start,
                              @Param("end")   Instant end);

    @Query("SELECT COUNT(u) FROM UsersEntity u WHERE u.createdAt < :asOf AND u.isActive = true")
    long countActiveUsersAt(@Param("asOf") Instant asOf);


    boolean existsByEmail(String email);
}
