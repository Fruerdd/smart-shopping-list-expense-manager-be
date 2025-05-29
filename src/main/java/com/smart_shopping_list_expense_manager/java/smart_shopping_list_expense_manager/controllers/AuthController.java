package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.FriendsEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.ReferralEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.enums.ReferralStatusEnum;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.FriendsRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.ReferralRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200", 
             allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository;
    private final FriendsRepository friendsRepository;
    private final ReferralRepository referralRepository;

    public AuthController(AuthService authService, UsersRepository usersRepository, FriendsRepository friendsRepository, ReferralRepository referralRepository) {
        this.authService = authService;
        this.usersRepository = usersRepository;
        this.friendsRepository = friendsRepository;
        this.referralRepository = referralRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDto) {
        String result = authService.register(registerDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthDTO authDto) {
        AuthResponse response = authService.login(authDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDTO dto = getResponseDTO(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile/me")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        UserDTO dto = getResponseDTO(user);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable String id, @RequestBody UserDTO userDTO) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(currentUserEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this profile");
        }

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

        UserDTO responseDTO = getResponseDTO(updatedUser);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity<List<UserDTO>> getUserFriends(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Get friends in BOTH directions
        List<FriendsEntity> friendships1 = friendsRepository.findByUser(user);
        List<FriendsEntity> friendships2 = friendsRepository.findByFriend(user);

        Set<UserDTO> friendsSet = new HashSet<>();

        // Add friends where this user was the initiator
        friendships1.forEach(friendship -> {
            UserDTO dto = new UserDTO();
            dto.setId(friendship.getFriend().getUserId());
            dto.setEmail(friendship.getFriend().getEmail());
            dto.setName(friendship.getFriend().getName());
            dto.setAvatar(friendship.getFriend().getAvatar());
            dto.setShoppingLists(List.of());
            friendsSet.add(dto);
        });

        // Add friends where this user was added as friend
        friendships2.forEach(friendship -> {
            UserDTO dto = new UserDTO();
            dto.setId(friendship.getUser().getUserId());
            dto.setEmail(friendship.getUser().getEmail());
            dto.setName(friendship.getUser().getName());
            dto.setAvatar(friendship.getUser().getAvatar());
            dto.setShoppingLists(List.of());
            friendsSet.add(dto);
        });

        return ResponseEntity.ok(new ArrayList<>(friendsSet));
    }

    @GetMapping("/statistics/{id}")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // TODO: Replace with actual statistics calculation
        UserStatisticsDTO statistics = new UserStatisticsDTO();
        statistics.setUserId(user.getUserId());
        statistics.setTotalLists(0);
        statistics.setTotalItems(0);
        statistics.setTotalSpent(0.0);
        statistics.setAverageSpentPerList(0.0);
        statistics.setMostFrequentStore("N/A");
        statistics.setMostBoughtItem("N/A");

        return ResponseEntity.ok(statistics);
    }

    @PatchMapping("/profile/{id}")
    public ResponseEntity<UserDTO> patchUserProfile(@PathVariable String id, @RequestBody UserDTO userDTO) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(currentUserEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this profile");
        }

        // Only update fields that are present in the request
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

        UserDTO responseDTO = getResponseDTO(updatedUser);

        return ResponseEntity.ok(responseDTO);
    }

    private static UserDTO getResponseDTO(UsersEntity updatedUser) {
        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(updatedUser.getUserId());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setName(updatedUser.getName());
        responseDTO.setPhone(updatedUser.getPhoneNumber());
        responseDTO.setAddress(updatedUser.getLocation());
        responseDTO.setAvatar(updatedUser.getAvatar());
        responseDTO.setBonus_points(updatedUser.getBonusPoints());
        responseDTO.setLoyaltyPoints(updatedUser.getBonusPoints());
        responseDTO.setCouponCode(updatedUser.getReferralCode() != null ? updatedUser.getReferralCode() : updatedUser.getPromoCode());
        responseDTO.setCreditsAvailable(updatedUser.getBonusPoints() != null ? updatedUser.getBonusPoints() * 0.05 : 0.0);
        responseDTO.setQrCodeValue(updatedUser.getPromoCode() != null ? updatedUser.getPromoCode() : updatedUser.getUserId().toString());
        responseDTO.setShoppingLists(List.of());
        return responseDTO;
    }

    @GetMapping("/profile/loyalty-points/{id}")
    public ResponseEntity<Integer> getLoyaltyPoints(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Integer loyaltyPoints = user.getBonusPoints();
        if (loyaltyPoints == null) {
            loyaltyPoints = 0; // Default to 0 if not set
        }

        return ResponseEntity.ok(loyaltyPoints);
    }

    @PutMapping("/profile/loyalty-points/{id}")
    public ResponseEntity<Integer> updateLoyaltyPoints(@PathVariable String id, @RequestParam Integer points) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (points < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Points cannot be negative");
        }

        user.setBonusPoints(points);
        UsersEntity updatedUser = usersRepository.save(user);

        return ResponseEntity.ok(updatedUser.getBonusPoints());
    }

    @GetMapping("/profile/reviews/{id}")
    public ResponseEntity<ReviewDTO> getUserReviews(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        return ResponseEntity.ok(new ReviewDTO(
                user.getName(),
                user.getReviewScore(),
                user.getReviewContext()
        ));
    }

    @PostMapping("/profile/reviews/{id}")
    public ResponseEntity<ReviewDTO> createUserReview(@PathVariable String id, @RequestBody ReviewDTO reviewDTO) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        }
        catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setReviewScore(reviewDTO.getReviewScore());
        user.setReviewContext(reviewDTO.getReviewContext());
        usersRepository.save(user);

        return ResponseEntity.ok(new ReviewDTO(
                user.getName(),
                user.getReviewScore(),
                user.getReviewContext()
        ));
    }

    @PostMapping("/profile/referral/{id}")
    public ResponseEntity<String> applyReferralCode(@PathVariable String id, @RequestParam String referralCode) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(currentUserEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to apply referral code for this user");
        }

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
        referral.setStatus(ReferralStatusEnum.REGISTERED); // User has registered using referral code
        referral.setRewardEarned(100); // 100 points for successful referral
        referralRepository.save(referral);

        // Award points to referrer
        int referrerPoints = referrer.getBonusPoints() != null ? referrer.getBonusPoints() : 0;
        referrer.setBonusPoints(referrerPoints + 100);
        usersRepository.save(referrer);

        // Award smaller bonus to new user
        int userPoints = user.getBonusPoints() != null ? user.getBonusPoints() : 0;
        user.setBonusPoints(userPoints + 25);
        usersRepository.save(user);
        
        return ResponseEntity.ok("Referral code applied successfully! You earned 25 bonus points, and the referrer earned 100 points.");
    }

    @GetMapping("/profile/referral-code/{id}")
    public ResponseEntity<String> getUserReferralCode(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String couponCode = user.getReferralCode() != null ? user.getReferralCode() : 
                           (user.getPromoCode() != null ? user.getPromoCode() : "YOUR" + user.getUserId().toString().substring(0, 4).toUpperCase());

        return ResponseEntity.ok(couponCode);
    }

    // Utility method to award loyalty points for various activities
    @PostMapping("/profile/award-points/{id}")
    public ResponseEntity<String> awardLoyaltyPoints(@PathVariable String id, 
                                                    @RequestParam String activity, 
                                                    @RequestParam(defaultValue = "1") int count) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int pointsToAward = 0;
        String message;

        switch (activity.toLowerCase()) {
            case "create_list":
                pointsToAward = 10 * count; // 10 points per list created
                message = String.format("Earned %d points for creating %d shopping list(s)!", pointsToAward, count);
                break;
            case "add_favorite_product":
                // Small chance (30%) to get points
                if (Math.random() < 0.3) {
                    pointsToAward = 2 * count; // 2 points per favorite product
                    message = String.format("Lucky! Earned %d points for adding %d favorite product(s)!", pointsToAward, count);
                } else {
                    message = "Added to favorites - no points this time, but keep adding for chances to earn points!";
                }
                break;
            case "add_favorite_store":
                // Small chance (20%) to get points
                if (Math.random() < 0.2) {
                    pointsToAward = 5 * count; // 5 points per favorite store
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

        return ResponseEntity.ok(message);
    }

    @PostMapping("/profile/upload-picture/{id}")
    public ResponseEntity<String> uploadProfilePicture(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid UUID format");
        }

        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!user.getEmail().equals(currentUserEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this profile picture");
        }

        // Validate file
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be an image");
        }

        // Validate file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must be less than 5MB");
        }

        try {
            // Create uploads directory if it doesn't exist
            String uploadDir = "uploads/profile-images";
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = "profile_" + uuid + "_" + System.currentTimeMillis() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update user avatar path in database (use relative path for web access)
            String avatarPath = "/uploads/profile-images/" + fileName;
            user.setAvatar(avatarPath);
            usersRepository.save(user);

            return ResponseEntity.ok(avatarPath);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search query cannot be empty");
        }

        List<UsersEntity> users = usersRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);

        List<UserDTO> userDTOs = users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getUserId());
                    dto.setEmail(user.getEmail());
                    dto.setName(user.getName());
                    dto.setAvatar(user.getAvatar());
                    dto.setShoppingLists(List.of());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(userDTOs);
    }
}
        