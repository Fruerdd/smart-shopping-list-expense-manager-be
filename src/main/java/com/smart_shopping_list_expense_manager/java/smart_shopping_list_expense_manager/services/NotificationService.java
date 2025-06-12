package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.NotificationDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.NotificationsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.NotificationsRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationsRepository notificationsRepository;
    private final UsersRepository usersRepository;

    public static final String FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String COLLABORATOR_ADDED = "COLLABORATOR_ADDED";
    public static final String REFERRAL_REWARD = "REFERRAL_REWARD";
    public static final String SYSTEM_MESSAGE = "SYSTEM_MESSAGE";

    public NotificationService(NotificationsRepository notificationsRepository, 
                             UsersRepository usersRepository) {
        this.notificationsRepository = notificationsRepository;
        this.usersRepository = usersRepository;
    }

    public List<NotificationDTO> getUserNotifications(UUID userId) {
        UsersEntity user = validateUserAccess(userId);
        
        List<NotificationsEntity> notifications = notificationsRepository.findByDestinationOrderByCreatedAtDesc(user);
        return notifications.stream().map(this::mapToNotificationDTO).toList();
    }

    public List<NotificationDTO> getUnreadNotifications(UUID userId) {
        UsersEntity user = validateUserAccess(userId);
        
        List<NotificationsEntity> notifications = notificationsRepository.findByDestinationAndIsReadFalseOrderByCreatedAtDesc(user);
        return notifications.stream().map(this::mapToNotificationDTO).toList();
    }

    public long getUnreadNotificationCount(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        return notificationsRepository.countByDestinationAndIsReadFalse(user);
    }

    public String markNotificationAsRead(UUID notificationId) {
        NotificationsEntity notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        validateUserAccess(notification.getDestination().getUserId());

        notification.setIsRead(true);
        notificationsRepository.save(notification);

        return "Notification marked as read";
    }

    @Transactional
    public String markAllNotificationsAsRead(UUID userId) {
        UsersEntity user = validateUserAccess(userId);
        
        int updatedCount = notificationsRepository.markAllAsReadForUserExcludingFriendRequests(user);
        
        return String.format("Marked %d notifications as read (excluding friend requests that require explicit response)", updatedCount);
    }

    public void createFriendRequestNotification(UsersEntity sender, UsersEntity receiver) {
        if (notificationsRepository.findByDestinationAndSourceAndNotificationTypeAndIsReadFalse(
                receiver, sender, FRIEND_REQUEST).isPresent()) {
            return;
        }

        NotificationsEntity notification = new NotificationsEntity();
        notification.setDestination(receiver);
        notification.setSource(sender);
        notification.setTitle("New Friend Request");
        notification.setMessage(sender.getName() + " sent you a friend request!");
        notification.setNotificationType(FRIEND_REQUEST);
        notification.setIsRead(false);

        notificationsRepository.save(notification);
    }

    public void createCollaboratorAddedNotification(UsersEntity listOwner, UsersEntity collaborator, String listName) {
        NotificationsEntity notification = new NotificationsEntity();
        notification.setDestination(collaborator);
        notification.setSource(listOwner);
        notification.setTitle("Added to Shopping List");
        notification.setMessage(listOwner.getName() + " added you as a collaborator to the list: " + listName);
        notification.setNotificationType(COLLABORATOR_ADDED);
        notification.setIsRead(false);

        notificationsRepository.save(notification);
    }

    public void createReferralRewardNotification(UsersEntity user, int pointsEarned, String referredUserName) {
        NotificationsEntity notification = new NotificationsEntity();
        notification.setDestination(user);
        notification.setSource(user);
        notification.setTitle("Referral Reward!");
        notification.setMessage("You earned " + pointsEarned + " points for referring " + referredUserName + "!");
        notification.setNotificationType(REFERRAL_REWARD);
        notification.setIsRead(false);

        notificationsRepository.save(notification);
    }

    public void createSystemNotification(UsersEntity user, String title, String message) {
        NotificationsEntity notification = new NotificationsEntity();
        notification.setDestination(user);
        notification.setSource(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(SYSTEM_MESSAGE);
        notification.setIsRead(false);

        notificationsRepository.save(notification);
    }

    private UsersEntity validateUserAccess(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(currentUserEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access these notifications");
        }

        return user;
    }

    private NotificationDTO mapToNotificationDTO(NotificationsEntity notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setSourceUserId(notification.getSource().getUserId());
        dto.setSourceUserName(notification.getSource().getName());
        dto.setSourceUserAvatar(notification.getSource().getAvatar());
        dto.setDestinationUserId(notification.getDestination().getUserId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setNotificationType(notification.getNotificationType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
} 