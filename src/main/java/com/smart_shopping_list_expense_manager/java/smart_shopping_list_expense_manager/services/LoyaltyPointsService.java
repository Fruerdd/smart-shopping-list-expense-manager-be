package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ReferralEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ReferralRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class LoyaltyPointsService {

    private final UsersRepository usersRepository;
    private final ReferralRepository referralRepository;
    private final NotificationService notificationService;

    public LoyaltyPointsService(UsersRepository usersRepository, ReferralRepository referralRepository, NotificationService notificationService) {
        this.usersRepository = usersRepository;
        this.referralRepository = referralRepository;
        this.notificationService = notificationService;
    }

    public Integer getLoyaltyPoints(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Integer loyaltyPoints = user.getBonusPoints();
        return loyaltyPoints != null ? loyaltyPoints : 0;
    }

    public Integer updateLoyaltyPoints(UUID userId, Integer points) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (points < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Points cannot be negative");
        }

        user.setBonusPoints(points);
        UsersEntity updatedUser = usersRepository.save(user);

        return updatedUser.getBonusPoints();
    }

    public String applyReferralCode(UUID userId, String referralCode) {
        UsersEntity user = validateUserAccess(userId);

        // Check if user has already used a referral code
        if (referralRepository.findByReferredUserUserId(user.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already used a referral code");
        }

        if (referralCode == null || referralCode.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Referral code cannot be empty");
        }

        // Find the referrer by their referral code
        UsersEntity referrer = usersRepository.findByReferralCode(referralCode)
                .orElse(usersRepository.findByPromoCode(referralCode).orElse(null));

        if (referrer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid referral code");
        }

        // Check if user is trying to use their own referral code
        if (referrer.getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot use your own referral code");
        }

        // Create referral record
        ReferralEntity referral = new ReferralEntity();
        referral.setReferrer(referrer);
        referral.setReferredUser(user);
        referral.setStatus(ReferralStatusEnum.REGISTERED);
        referral.setRewardEarned(100);
        referralRepository.save(referral);

        // Award points to referrer
        int referrerPoints = referrer.getBonusPoints() != null ? referrer.getBonusPoints() : 0;
        referrer.setBonusPoints(referrerPoints + 100);
        usersRepository.save(referrer);

        // Award smaller bonus to new user
        int userPoints = user.getBonusPoints() != null ? user.getBonusPoints() : 0;
        user.setBonusPoints(userPoints + 25);
        usersRepository.save(user);

        // Create notifications for both users
        notificationService.createReferralRewardNotification(referrer, 100, user.getName());
        notificationService.createSystemNotification(user, "Welcome Bonus!", 
            "Welcome! You earned 25 bonus points for using a referral code.");

        return "Referral code applied successfully! You earned 25 bonus points, and the referrer earned 100 points.";
    }

    public String getUserReferralCode(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getReferralCode() != null ? user.getReferralCode() :
                (user.getPromoCode() != null ? user.getPromoCode() : "YOUR" + user.getUserId().toString().substring(0, 4).toUpperCase());
    }

    public String awardLoyaltyPoints(UUID userId, String activity, int count) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int pointsToAward = 0;
        String message;

        switch (activity.toLowerCase()) {
            case "create_list":
                pointsToAward = 10 * count;
                message = String.format("Earned %d points for creating %d shopping list(s)!", pointsToAward, count);
                break;
            case "add_favorite_product":
                if (Math.random() < 0.3) {
                    pointsToAward = 2 * count;
                    message = String.format("Lucky! Earned %d points for adding %d favorite product(s)!", pointsToAward, count);
                } else {
                    message = "Added to favorites - no points this time, but keep adding for chances to earn points!";
                }
                break;
            case "add_favorite_store":
                if (Math.random() < 0.2) {
                    pointsToAward = 5 * count;
                    message = String.format("Lucky! Earned %d points for adding %d favorite store(s)!", pointsToAward, count);
                } else {
                    message = "Added to favorites - no points this time, but keep adding for chances to earn points!";
                }
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activity type");
        }

        if (pointsToAward > 0) {
            int currentPoints = user.getBonusPoints() != null ? user.getBonusPoints() : 0;
            user.setBonusPoints(currentPoints + pointsToAward);
            usersRepository.save(user);
        }

        return message;
    }

    private UsersEntity validateUserAccess(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(currentUserEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to apply referral code for this user");
        }

        return user;
    }
} 