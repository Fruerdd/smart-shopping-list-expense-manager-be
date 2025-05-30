package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UsersRepository             usersRepo;
    private final FriendsRepository           friendsRepo;
    private final ShoppingListRepository      listRepo;
    private final ShoppingListItemRepository  itemRepo;
    private final StorePriceRepository        priceRepo;
    private final NotificationService         notificationService;

    public UserProfileService(
            UsersRepository usersRepo,
            FriendsRepository friendsRepo,
            ShoppingListRepository listRepo,
            ShoppingListItemRepository itemRepo,
            StorePriceRepository priceRepo,
            NotificationService notificationService
    ) {
        this.usersRepo           = usersRepo;
        this.friendsRepo         = friendsRepo;
        this.listRepo            = listRepo;
        this.itemRepo            = itemRepo;
        this.priceRepo           = priceRepo;
        this.notificationService = notificationService;
    }

    // --- Basic Profile CRUD -----------------------------------------------

    public UserDTO getUserProfile(UUID userId) {
        return toDto(findUserOr404(userId));
    }

    public UserDTO getCurrentUserProfile() {
        String email = currentEmail();
        return toDto(
            usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"))
        );
    }

    public UserDTO updateUserProfile(UUID userId, UserDTO in) {
        UsersEntity u = validateAccess(userId);
        Optional.ofNullable(in.getName()).ifPresent(u::setName);
        Optional.ofNullable(in.getPhone()).ifPresent(u::setPhoneNumber);
        Optional.ofNullable(in.getAddress()).ifPresent(u::setLocation);
        Optional.ofNullable(in.getAvatar()).ifPresent(u::setAvatar);
        return toDto(usersRepo.save(u));
    }

    // --- Friends ----------------------------------------------------------

    public List<UserDTO> getUserFriends(UUID userId) {
        UsersEntity user = findUserOr404(userId);
        Set<FriendsEntity> all = new HashSet<>();
        all.addAll(friendsRepo.findByUser(user));
        all.addAll(friendsRepo.findByFriend(user));
        return all.stream()
            .map(f -> {
                UsersEntity other = f.getUser().equals(user) ? f.getFriend() : f.getUser();
                return toDto(other);
            })
            .collect(Collectors.toList());
    }

    public String sendFriendRequest(UUID userId, UUID friendId) {
        UsersEntity user   = validateAccess(userId);
        UsersEntity target = findUserOr404(friendId);

        if (user.equals(target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot friend yourself");
        }
        boolean exists = friendsRepo.findFriendshipBetweenUsers(user, target).isPresent();
        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already friends");
        }
        notificationService.createFriendRequestNotification(user, target);
        return "Friend request sent";
    }

    public String acceptFriendRequest(UUID userId, UUID requesterId) {
        UsersEntity me  = validateAccess(userId);
        UsersEntity you = findUserOr404(requesterId);
        if (friendsRepo.findFriendshipBetweenUsers(me, you).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already friends");
        }
        FriendsEntity f = new FriendsEntity();
        f.setUser(you);
        f.setFriend(me);
        friendsRepo.save(f);
        return "Friend request accepted";
    }

    public String removeFriend(UUID userId, UUID friendId) {
        UsersEntity me  = validateAccess(userId);
        UsersEntity you = findUserOr404(friendId);

        // delete both possible directions
        friendsRepo.findByUserAndFriend(me, you).ifPresent(friendsRepo::delete);
        friendsRepo.findByUserAndFriend(you, me).ifPresent(friendsRepo::delete);
        return "Friend removed";
    }

    // --- Search -----------------------------------------------------------

    public List<UserDTO> searchUsers(String q) {
        if (q == null || q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty query");
        }
        return usersRepo
            .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    // --- Statistics -------------------------------------------------------

    public UserStatisticsDTO getUserStatistics(UUID userId) {
        UsersEntity user = findUserOr404(userId);

        long totalLists = listRepo.countByOwnerUserId(userId);
        long totalItems = itemRepo.countByShoppingListOwnerUserId(userId);

        // compute total spent
        List<ShoppingListItemEntity> items = itemRepo.findByShoppingListOwnerUserId(userId);
        BigDecimal totalSpent = items.stream()
            .map(it -> priceRepo
                          .findFirstByProductProductIdOrderByCreatedAtDesc(it.getProduct().getProductId())
                          .map(StorePriceEntity::getPrice)
                          .orElse(BigDecimal.ZERO)
                        .multiply(it.getQuantity()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        double spent = totalSpent.doubleValue();
        double avg   = totalLists > 0 ? spent / totalLists : 0.0;

        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setUserId(userId);
        stats.setTotalLists((int) totalLists);
        stats.setTotalItems((int) totalItems);
        stats.setTotalSpent(spent);
        stats.setAverageSpentPerList(avg);
        return stats;
    }

    // --- Internals --------------------------------------------------------

    private UsersEntity findUserOr404(UUID id) {
        return usersRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String currentEmail() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private UsersEntity validateAccess(UUID userId) {
        UsersEntity u = findUserOr404(userId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!u.getEmail().equals(auth.getName()) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
        }
        return u;
    }

    private UserDTO toDto(UsersEntity u) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getUserId());
        dto.setEmail(u.getEmail());
        dto.setName(u.getName());
        dto.setPhone(u.getPhoneNumber());
        dto.setAddress(u.getLocation());
        dto.setAvatar(u.getAvatar());
        dto.setBonus_points(u.getBonusPoints());
        dto.setLoyaltyPoints(u.getBonusPoints());
        dto.setCouponCode(u.getReferralCode() != null ? u.getReferralCode() : u.getPromoCode());
        dto.setCreditsAvailable(u.getBonusPoints() != null ? u.getBonusPoints() * 0.05 : 0.0);
        dto.setQrCodeValue(u.getPromoCode() != null ? u.getPromoCode() : u.getUserId().toString());
        dto.setShoppingLists(Collections.emptyList());
        return dto;
    }
}
