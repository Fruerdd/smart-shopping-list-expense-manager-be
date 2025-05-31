package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.controllers;

import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReferralDTO;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.ReferralResponse;
import com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.services.ReferralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/referral")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @PostMapping("/invite")
    public ResponseEntity<ReferralResponse> sendReferral(@RequestBody ReferralDTO referralDto) {
        ReferralResponse response = referralService.createReferral(referralDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/complete/{referredUserId}")
    public ResponseEntity<ReferralResponse> markAsCompleted(@PathVariable UUID referredUserId) {
        ReferralResponse response = referralService.completeReferral(referredUserId);
        return ResponseEntity.ok(response);
    }

}
