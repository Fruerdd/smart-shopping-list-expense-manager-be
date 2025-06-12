package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.AuthDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.AuthResponse;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.RegisterDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Random;

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

    public String register(RegisterDTO dto) {
        if (usersRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        UsersEntity user = new UsersEntity();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setIsActive(true);
        user.setUserType("USER");
        user.setBonusPoints(0);
        user.setDeviceInfo("Change this yourself");
        user.setPhoneNumber("1234567890");
        user.setReferralCode(generateReferralCode());
        user.setPromoCode(generatePromoCode());
        user.setAvatar("https://images.icon-icons.com/1378/PNG/512/avatardefault_92824.png");
        user.setReviewScore(0.0);
        user.setReviewContext("No reviews yet");
        user.setLocation("Change this yourself");

        usersRepository.save(user);
        return "User registered successfully";
    }

    private String generateReferralCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String randomNumberString = String.format("%04d", randomNumber);
        return "REF" + randomNumberString.toUpperCase();
    }

    private String generatePromoCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String randomNumberString = String.format("%04d", randomNumber);
        return "PROMO" + randomNumberString.toUpperCase();
    }

    public AuthResponse login(AuthDTO authDTO) {
        UsersEntity user = usersRepository.findByEmail(authDTO.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(
                token,
                "Login successful",
                user.getUserType()
        );
    }
}
