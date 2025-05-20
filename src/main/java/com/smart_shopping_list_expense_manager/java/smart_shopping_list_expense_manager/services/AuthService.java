package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.AuthDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.AuthResponse;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.RegisterDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
    }

    /**
     * Registers a new user by hashing their password.
     */
    public String register(RegisterDTO dto) {
        if (usersRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        UsersEntity user = new UsersEntity();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setIsActive(true);

        usersRepository.save(user);
        return "User registered successfully";
    }

    /**
     * Logs in an existing user by validating credentials manually.
     */
    public AuthResponse login(AuthDTO authDTO) {
        UsersEntity user = usersRepository.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(
                token,
                "Login successful",
                user.getUserType()     // ← must match the DTO’s `userType` field
        );
    }
}
