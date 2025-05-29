package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.NotificationsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationsRepository extends JpaRepository<NotificationsEntity, UUID> {
    
    // Find all notifications for a user, ordered by creation date (newest first)
    List<NotificationsEntity> findByDestinationOrderByCreatedAtDesc(UsersEntity destination);
    
    // Find unread notifications for a user
    List<NotificationsEntity> findByDestinationAndIsReadFalseOrderByCreatedAtDesc(UsersEntity destination);
    
    // Find notifications by type for a user
    List<NotificationsEntity> findByDestinationAndNotificationTypeOrderByCreatedAtDesc(UsersEntity destination, String notificationType);
    
    // Count unread notifications for a user
    long countByDestinationAndIsReadFalse(UsersEntity destination);
    
    // Check if specific notification exists (to avoid duplicates)
    Optional<NotificationsEntity> findByDestinationAndSourceAndNotificationTypeAndIsReadFalse(
        UsersEntity destination, UsersEntity source, String notificationType);
    
    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE NotificationsEntity n SET n.isRead = true WHERE n.destination = :destination AND n.isRead = false")
    int markAllAsReadForUser(@Param("destination") UsersEntity destination);

    // Mark all notifications as read for a user, excluding friend requests that require explicit response
    @Modifying
    @Query("UPDATE NotificationsEntity n SET n.isRead = true WHERE n.destination = :destination AND n.isRead = false AND n.notificationType != 'FRIEND_REQUEST'")
    int markAllAsReadForUserExcludingFriendRequests(@Param("destination") UsersEntity destination);
}
