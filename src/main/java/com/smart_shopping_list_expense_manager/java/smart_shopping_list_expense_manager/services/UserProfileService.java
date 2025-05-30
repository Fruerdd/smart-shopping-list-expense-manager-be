package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.UserDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.UserStatisticsDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FriendsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.FriendsRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserProfileService {

    private final UsersRepository usersRepository;
    private final FriendsRepository friendsRepository;
    private final NotificationService notificationService;

    public UserProfileService(UsersRepository usersRepository,
                              FriendsRepository friendsRepository,
                              NotificationService notificationService) {
        this.usersRepository = usersRepository;
        this.friendsRepository = friendsRepository;
        this.notificationService = notificationService;
    }

    public UserDTO getUserProfile(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToUserDTO(user);
    }

    public UserDTO getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        return mapToUserDTO(user);
    }

    public UserDTO updateUserProfile(UUID userId, UserDTO userDTO) {
        UsersEntity user = validateUserAccess(userId);

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhoneNumber(userDTO.getPhone());
        }
        if (userDTO.getAddress() != null) {
            user.setLocation(userDTO.getAddress());
        }

        UsersEntity updatedUser = usersRepository.save(user);
        return mapToUserDTO(updatedUser);
    }

    public UserDTO patchUserProfile(UUID userId, UserDTO userDTO) {
        UsersEntity user = validateUserAccess(userId);

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhone() != null) {
            user.setPhoneNumber(userDTO.getPhone());
        }
        if (userDTO.getAddress() != null) {
            user.setLocation(userDTO.getAddress());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        UsersEntity updatedUser = usersRepository.save(user);
        return mapToUserDTO(updatedUser);
    }

    public List<UserDTO> getUserFriends(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<FriendsEntity> friendships1 = friendsRepository.findByUser(user);
        List<FriendsEntity> friendships2 = friendsRepository.findByFriend(user);

        Set<UserDTO> friendsSet = new HashSet<>();
        friendships1.forEach(f -> friendsSet.add(mapToBasicUserDTO(f.getFriend())));
        friendships2.forEach(f -> friendsSet.add(mapToBasicUserDTO(f.getUser())));

        return new ArrayList<>(friendsSet);
    }

    public String sendFriendRequest(UUID userId, UUID friendId) {
        UsersEntity user = validateUserAccess(userId);

        if (userId.equals(friendId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a friend request to yourself");
        }

        UsersEntity friendUser = usersRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Optional<FriendsEntity> existingFriendship =
                friendsRepository.findFriendshipBetweenUsers(user, friendUser);
        if (existingFriendship.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already friends with this user");
        }

        notificationService.createFriendRequestNotification(user, friendUser);
        return "Friend request sent successfully!";
    }

    public String acceptFriendRequest(UUID userId, UUID requesterId) {
        UsersEntity user = validateUserAccess(userId);

        UsersEntity requester = usersRepository.findById(requesterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester not found"));

        Optional<FriendsEntity> existingFriendship =
                friendsRepository.findFriendshipBetweenUsers(user, requester);
        if (existingFriendship.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already friends with this user");
        }

        FriendsEntity friendship = new FriendsEntity();
        friendship.setUser(requester);
        friendship.setFriend(user);
        friendsRepository.save(friendship);

        return "Friend request accepted!";
    }

    public String declineFriendRequest(UUID userId, UUID requesterId) {
        validateUserAccess(userId);
        usersRepository.findById(requesterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester not found"));
        return "Friend request declined.";
    }

    public String removeFriend(UUID userId, UUID friendId) {
        UsersEntity user = validateUserAccess(userId);

        UsersEntity friendUser = usersRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend user not found"));

        boolean removed = false;
        Optional<FriendsEntity> f1 = friendsRepository.findByUserAndFriend(user, friendUser);
        if (f1.isPresent()) {
            friendsRepository.delete(f1.get());
            removed = true;
        }
        Optional<FriendsEntity> f2 = friendsRepository.findByUserAndFriend(friendUser, user);
        if (f2.isPresent()) {
            friendsRepository.delete(f2.get());
            removed = true;
        }
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found");
        }

        return "Friend removed successfully!";
    }

    public UserStatisticsDTO getUserStatistics(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setUserId(user.getUserId());
        stats.setTotalLists(0);
        stats.setTotalItems(0);
        stats.setTotalSpent(0.0);
        stats.setAverageSpentPerList(0.0);
        stats.setMostFrequentStore("N/A");
        stats.setMostBoughtItem("N/A");
        return stats;
    }

    public List<UserDTO> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be empty");
        }
        List<UsersEntity> users =
                usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
        return users.stream()
                .map(this::mapToBasicUserDTO)
                .toList();
    }

    private UsersEntity validateUserAccess(UUID userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!user.getEmail().equals(currentEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this profile");
        }
        return user;
    }

    /**
     * Legacy getCurrent() fixed to use mapToUserDTO(...)
     */
    public UserDTO getCurrent() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        UsersEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
        return mapToUserDTO(user);
    }

    private UserDTO mapToUserDTO(UsersEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhoneNumber());
        dto.setAddress(user.getLocation());
        dto.setAvatar(user.getAvatar());
        dto.setBonus_points(user.getBonusPoints());
        dto.setLoyaltyPoints(user.getBonusPoints());
        dto.setCouponCode(user.getReferralCode() != null ? user.getReferralCode() : user.getPromoCode());
        dto.setCreditsAvailable(user.getBonusPoints() != null ? user.getBonusPoints() * 0.05 : 0.0);
        dto.setQrCodeValue(user.getPromoCode() != null ? user.getPromoCode() : user.getUserId().toString());
        dto.setShoppingLists(List.of());
        return dto;
    }

    private UserDTO mapToBasicUserDTO(UsersEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAvatar(user.getAvatar());
        dto.setShoppingLists(List.of());
        return dto;
    }
}
