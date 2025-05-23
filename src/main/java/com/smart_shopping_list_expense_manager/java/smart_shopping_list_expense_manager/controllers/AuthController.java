package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.*;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
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

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository;

    public AuthController(AuthService authService, UsersRepository usersRepository) {
        this.authService = authService;
        this.usersRepository = usersRepository;
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

        // TODO: Replace with actual friends implementation
        // For now, return all users except the current user as potential friends
        List<UserDTO> friends = usersRepository.findAll().stream()
                .filter(u -> !u.getUserId().equals(uuid))
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getUserId());
                    dto.setEmail(u.getEmail());
                    dto.setName(u.getName());
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
}