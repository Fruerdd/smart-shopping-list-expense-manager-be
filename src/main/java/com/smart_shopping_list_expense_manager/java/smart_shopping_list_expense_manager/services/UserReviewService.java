package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReviewDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.entities.UsersEntity;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.repositories.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserReviewService {

    private final UsersRepository usersRepository;

    public UserReviewService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public ReviewDTO getUserReviews(UUID userId) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new ReviewDTO(
                user.getName(),
                user.getReviewScore(),
                user.getReviewContext()
        );
    }

    public ReviewDTO createUserReview(UUID userId, ReviewDTO reviewDTO) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setReviewScore(reviewDTO.getReviewScore());
        user.setReviewContext(reviewDTO.getReviewContext());
        usersRepository.save(user);

        return new ReviewDTO(
                user.getName(),
                user.getReviewScore(),
                user.getReviewContext()
        );
    }
} 