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
    
    List<NotificationsEntity> findByDestinationOrderByCreatedAtDesc(UsersEntity destination);
    
    List<NotificationsEntity> findByDestinationAndIsReadFalseOrderByCreatedAtDesc(UsersEntity destination);
    
    List<NotificationsEntity> findByDestinationAndNotificationTypeOrderByCreatedAtDesc(UsersEntity destination, String notificationType);
    
    long countByDestinationAndIsReadFalse(UsersEntity destination);
    
    Optional<NotificationsEntity> findByDestinationAndSourceAndNotificationTypeAndIsReadFalse(
        UsersEntity destination, UsersEntity source, String notificationType);
    
    @Modifying
    @Query("UPDATE NotificationsEntity n SET n.isRead = true WHERE n.destination = :destination AND n.isRead = false")
    int markAllAsReadForUser(@Param("destination") UsersEntity destination);

    @Modifying
    @Query("UPDATE NotificationsEntity n SET n.isRead = true WHERE n.destination = :destination AND n.isRead = false AND n.notificationType != 'FRIEND_REQUEST'")
    int markAllAsReadForUserExcludingFriendRequests(@Param("destination") UsersEntity destination);
}
