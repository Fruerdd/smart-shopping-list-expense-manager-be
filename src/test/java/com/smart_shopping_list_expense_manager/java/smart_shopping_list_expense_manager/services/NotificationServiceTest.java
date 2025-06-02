package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.NotificationsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.NotificationsRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationsRepository notificationsRepository;

    @Mock
    private UsersRepository usersRepository;

    private UsersEntity user;
    private UsersEntity adminUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new UsersEntity();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setName("Test User");

        adminUser = new UsersEntity();
        adminUser.setUserId(UUID.randomUUID());
        adminUser.setEmail("admin@example.com");
        adminUser.setName("Admin User");

        SecurityContextHolder.clearContext();
    }

    void authenticateAs(UsersEntity u, boolean isAdmin) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        u.getEmail(),
                        null,
                        isAdmin ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) : List.of()
                )
        );
    }

    @Test
    void getUserNotifications_success() {
        authenticateAs(user, false);
        NotificationsEntity entity = new NotificationsEntity();
        entity.setId(UUID.randomUUID());
        entity.setSource(user);
        entity.setDestination(user);
        entity.setCreatedAt(Instant.now());

        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(notificationsRepository.findByDestinationOrderByCreatedAtDesc(user)).thenReturn(List.of(entity));

        var result = notificationService.getUserNotifications(user.getUserId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnreadNotifications_success() {
        authenticateAs(user, false);
        NotificationsEntity entity = new NotificationsEntity();
        entity.setId(UUID.randomUUID());
        entity.setSource(user);
        entity.setDestination(user);
        entity.setIsRead(false);
        entity.setCreatedAt(Instant.now());

        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(notificationsRepository.findByDestinationAndIsReadFalseOrderByCreatedAtDesc(user))
                .thenReturn(List.of(entity));

        var result = notificationService.getUnreadNotifications(user.getUserId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getUnreadNotificationCount_success() {
        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(notificationsRepository.countByDestinationAndIsReadFalse(user)).thenReturn(3L);

        long count = notificationService.getUnreadNotificationCount(user.getUserId());

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void markNotificationAsRead_success() {
        authenticateAs(user, false);
        NotificationsEntity entity = new NotificationsEntity();
        entity.setId(UUID.randomUUID());
        entity.setDestination(user);

        when(notificationsRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(notificationsRepository.save(entity)).thenReturn(entity);

        String result = notificationService.markNotificationAsRead(entity.getId());

        assertThat(result).isEqualTo("Notification marked as read");
        assertThat(entity.getIsRead()).isTrue();
    }

    @Test
    void markAllNotificationsAsRead_success() {
        authenticateAs(user, false);
        when(usersRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(notificationsRepository.markAllAsReadForUserExcludingFriendRequests(user)).thenReturn(2);

        String result = notificationService.markAllNotificationsAsRead(user.getUserId());

        assertThat(result).contains("Marked 2 notifications as read");
    }

    @Test
    void createFriendRequestNotification_savesWhenNotExists() {
        when(notificationsRepository.findByDestinationAndSourceAndNotificationTypeAndIsReadFalse(
                user, adminUser, NotificationService.FRIEND_REQUEST)).thenReturn(Optional.empty());

        notificationService.createFriendRequestNotification(adminUser, user);

        verify(notificationsRepository, times(1)).save(any(NotificationsEntity.class));
    }

    @Test
    void createFriendRequestNotification_doesNotSaveIfAlreadyExists() {
        when(notificationsRepository.findByDestinationAndSourceAndNotificationTypeAndIsReadFalse(
                user, adminUser, NotificationService.FRIEND_REQUEST)).thenReturn(Optional.of(new NotificationsEntity()));

        notificationService.createFriendRequestNotification(adminUser, user);

        verify(notificationsRepository, never()).save(any());
    }

    @Test
    void createCollaboratorAddedNotification_success() {
        notificationService.createCollaboratorAddedNotification(adminUser, user, "Groceries");
        verify(notificationsRepository).save(any());
    }

    @Test
    void createReferralRewardNotification_success() {
        notificationService.createReferralRewardNotification(user, 100, "Alice");
        verify(notificationsRepository).save(any());
    }

    @Test
    void createSystemNotification_success() {
        notificationService.createSystemNotification(user, "System Alert", "Something happened.");
        verify(notificationsRepository).save(any());
    }

    @Test
    void getUserNotifications_throwsWhenUserNotFound() {
        authenticateAs(user, false);
        UUID unknownId = UUID.randomUUID();
        when(usersRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.getUserNotifications(unknownId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUserNotifications_throwsWhenUserUnauthorized() {
        authenticateAs(user, false);
        UsersEntity anotherUser = new UsersEntity();
        anotherUser.setUserId(UUID.randomUUID());
        anotherUser.setEmail("other@example.com");

        when(usersRepository.findById(anotherUser.getUserId())).thenReturn(Optional.of(anotherUser));

        assertThatThrownBy(() -> notificationService.getUserNotifications(anotherUser.getUserId()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("permission");
    }
}
