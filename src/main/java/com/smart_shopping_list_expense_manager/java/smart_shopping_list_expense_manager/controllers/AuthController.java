package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.FriendsRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository;
    private final FriendsRepository friendsRepository;

    public AuthController(AuthService authService, UsersRepository usersRepository, FriendsRepository friendsRepository) {
        this.authService = authService;
        this.usersRepository = usersRepository;
        this.friendsRepository = friendsRepository;
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

        UserDTO dto = new UserDTO();
        dto.setId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhoneNumber());
        dto.setAddress(user.getLocation());
        dto.setBonus_points(user.getBonusPoints());
        dto.setShoppingLists(List.of());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile/me")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        UsersEntity user = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        UserDTO dto = new UserDTO();
        dto.setId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setShoppingLists(List.of());

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

        UsersEntity updatedUser = usersRepository.save(user);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(updatedUser.getUserId());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setName(updatedUser.getName());
        responseDTO.setShoppingLists(List.of());

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

        // Query for friendships where the current user is involved
        List<UserDTO> friends = friendsRepository.findByUser(user).stream()
                .map(friend -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(friend.getFriend().getUserId());
                    dto.setEmail(friend.getFriend().getEmail());
                    dto.setName(friend.getFriend().getName());
                    dto.setShoppingLists(List.of());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(friends);
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

    // TODO: Improve this method more
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
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

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

        UsersEntity updatedUser = usersRepository.save(user);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(updatedUser.getUserId());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setName(updatedUser.getName());
        responseDTO.setPhone(updatedUser.getPhoneNumber());
        responseDTO.setAddress(updatedUser.getLocation());
        responseDTO.setShoppingLists(List.of());

        return ResponseEntity.ok(responseDTO);
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
}
        