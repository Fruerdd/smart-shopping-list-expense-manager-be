package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.UserDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.UserStatisticsDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FriendsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ShoppingListItemEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.StorePriceEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UsersRepository       usersRepo;
    private final FriendsRepository     friendsRepo;
    private final ShoppingListRepository listRepo;
    private final ShoppingListItemRepository itemRepo;
    private final StorePriceRepository priceRepo;

    public UserProfileService(UsersRepository usersRepo,
                              FriendsRepository friendsRepo,
                              ShoppingListRepository listRepo,
                              ShoppingListItemRepository itemRepo,
                              StorePriceRepository priceRepo) {
        this.usersRepo   = usersRepo;
        this.friendsRepo = friendsRepo;
        this.listRepo    = listRepo;
        this.itemRepo    = itemRepo;
        this.priceRepo = priceRepo;
    }

    public UserDTO getById(UUID userId) {
        UsersEntity user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(user);
    }

    public UserDTO getCurrent() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        UsersEntity user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
        return toDto(user);
    }

    public UserDTO update(UUID userId, UserDTO in) {
        UsersEntity user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!user.getEmail().equals(auth.getName()) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission");
        }

        // only overwrite fields that arrived
        Optional.ofNullable(in.getName()).ifPresent(user::setName);
        Optional.ofNullable(in.getPhone()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(in.getAddress()).ifPresent(user::setLocation);
        Optional.ofNullable(in.getAvatar()).ifPresent(user::setAvatar);

        UsersEntity updated = usersRepo.save(user);
        return toDto(updated);
    }

    public List<UserDTO> getFriends(UUID userId) {
        UsersEntity user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // both directions
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

    public UserStatisticsDTO getStatistics(UUID userId) {
        long totalLists = listRepo.countByOwnerUserId(userId);
        long totalItems = itemRepo.countByShoppingListOwnerUserId(userId);

        // 1) fetch all items on this user's lists
        List<ShoppingListItemEntity> items =
                itemRepo.findByShoppingListOwnerUserId(userId);

        // 2) for each item, look up its latest store price and multiply by quantity
        BigDecimal totalCost = items.stream()
                .map(item -> {
                    UUID prodId = item.getProduct().getProductId();

                    // fetch the most recent StorePriceEntity
                    BigDecimal unitPrice = priceRepo
                            .findFirstByProductProductIdOrderByCreatedAtDesc(prodId)
                            .map(StorePriceEntity::getPrice)
                            .orElse(BigDecimal.ZERO);

                    // multiply price × quantity
                    return unitPrice.multiply(item.getQuantity());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double totalCostDouble = totalCost.doubleValue();
        double avgPerList = totalLists > 0
                ? totalCostDouble / totalLists
                : 0.0;

        UserStatisticsDTO stats = new UserStatisticsDTO();
        stats.setUserId(userId);
        stats.setTotalLists((int) totalLists);
        stats.setTotalItems((int) totalItems);
        stats.setTotalSpent(totalCostDouble);
        stats.setAverageSpentPerList(avgPerList);
        return stats;
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
        dto.setCouponCode(u.getReferralCode()!=null ? u.getReferralCode() : u.getPromoCode());
        dto.setCreditsAvailable(u.getBonusPoints()!=null ? u.getBonusPoints()*0.05 : 0.0);
        dto.setQrCodeValue(u.getPromoCode()!=null ? u.getPromoCode() : u.getUserId().toString());
        dto.setShoppingLists(Collections.emptyList());
        return dto;
    }
}
