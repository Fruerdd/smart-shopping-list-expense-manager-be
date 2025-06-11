package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserProfileService userProfileService;
    private final LoyaltyPointsService loyaltyPointsService;
    private final UserReviewService userReviewService;
    private final FileUploadService fileUploadService;
    private final NotificationService notificationService;

    public UserController(UserProfileService userProfileService,
                         LoyaltyPointsService loyaltyPointsService,
                         UserReviewService userReviewService,
                         FileUploadService fileUploadService,
                         NotificationService notificationService) {
        this.userProfileService = userProfileService;
        this.loyaltyPointsService = loyaltyPointsService;
        this.userReviewService = userReviewService;
        this.fileUploadService = fileUploadService;
        this.notificationService = notificationService;
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userProfileService.getUserProfile(uuid));
    }

    @GetMapping("/profile/me")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        return ResponseEntity.ok(userProfileService.getCurrentUserProfile());
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable String id, @RequestBody UserDTO userDTO) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userProfileService.updateUserProfile(uuid, userDTO));
    }

    @PatchMapping("/profile/{id}")
    public ResponseEntity<UserDTO> patchUserProfile(@PathVariable String id, @RequestBody UserDTO userDTO) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userProfileService.patchUserProfile(uuid, userDTO));
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity<List<UserDTO>> getUserFriends(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userProfileService.getUserFriends(uuid));
    }

    @PostMapping("/friends/{id}/request")
    public ResponseEntity<String> sendFriendRequest(@PathVariable String id, @RequestParam String friendId) {
        UUID uuid = parseUUID(id);
        UUID friendUuid = parseUUID(friendId);
        return ResponseEntity.ok(userProfileService.sendFriendRequest(uuid, friendUuid));
    }

    @PostMapping("/friends/{id}/accept")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable String id, @RequestParam String requesterId) {
        UUID uuid = parseUUID(id);
        UUID requesterUuid = parseUUID(requesterId);
        return ResponseEntity.ok(userProfileService.acceptFriendRequest(uuid, requesterUuid));
    }

    @PostMapping("/friends/{id}/decline")
    public ResponseEntity<String> declineFriendRequest(@PathVariable String id, @RequestParam String requesterId) {
        UUID uuid = parseUUID(id);
        UUID requesterUuid = parseUUID(requesterId);
        return ResponseEntity.ok(userProfileService.declineFriendRequest(uuid, requesterUuid));
    }

    @DeleteMapping("/friends/{id}/remove")
    public ResponseEntity<String> removeFriend(@PathVariable String id, @RequestParam String friendId) {
        UUID uuid = parseUUID(id);
        UUID friendUuid = parseUUID(friendId);
        return ResponseEntity.ok(userProfileService.removeFriend(uuid, friendUuid));
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userProfileService.getUserStatistics(uuid));
    }

    @GetMapping("/profile/loyalty-points/{id}")
    public ResponseEntity<Integer> getLoyaltyPoints(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(loyaltyPointsService.getLoyaltyPoints(uuid));
    }

    @PutMapping("/profile/loyalty-points/{id}")
    public ResponseEntity<Integer> updateLoyaltyPoints(@PathVariable String id, @RequestParam Integer points) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(loyaltyPointsService.updateLoyaltyPoints(uuid, points));
    }

    @PostMapping("/profile/award-points/{id}")
    public ResponseEntity<String> awardLoyaltyPoints(@PathVariable String id, 
                                                    @RequestParam String activity, 
                                                    @RequestParam(defaultValue = "1") int count) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(loyaltyPointsService.awardLoyaltyPoints(uuid, activity, count));
    }

    @PostMapping("/profile/referral/{id}")
    public ResponseEntity<String> applyReferralCode(@PathVariable String id, @RequestParam String referralCode) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(loyaltyPointsService.applyReferralCode(uuid, referralCode));
    }

    @GetMapping("/profile/referral-code/{id}")
    public ResponseEntity<String> getUserReferralCode(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(loyaltyPointsService.getUserReferralCode(uuid));
    }

    @GetMapping("/profile/reviews/{id}")
    public ResponseEntity<ReviewDTO> getUserReviews(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userReviewService.getUserReviews(uuid));
    }

    @PostMapping("/profile/reviews/{id}")
    public ResponseEntity<ReviewDTO> createUserReview(@PathVariable String id, @RequestBody ReviewDTO reviewDTO) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(userReviewService.createUserReview(uuid, reviewDTO));
    }

    @PostMapping("/profile/upload-picture/{id}")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(fileUploadService.uploadProfilePicture(uuid, file));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String q) {
        return ResponseEntity.ok(userProfileService.searchUsers(q));
    }

    @GetMapping("/notifications/{id}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(notificationService.getUserNotifications(uuid));
    }

    @GetMapping("/notifications/{id}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(notificationService.getUnreadNotifications(uuid));
    }

    @GetMapping("/notifications/{id}/count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(notificationService.getUnreadNotificationCount(uuid));
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable String notificationId) {
        UUID notificationUuid = parseUUID(notificationId);
        return ResponseEntity.ok(notificationService.markNotificationAsRead(notificationUuid));
    }

    @PatchMapping("/notifications/{id}/read-all")
    public ResponseEntity<String> markAllNotificationsAsRead(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        return ResponseEntity.ok(notificationService.markAllNotificationsAsRead(uuid));
    }

    private UUID parseUUID(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }
    }
} 