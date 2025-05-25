package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private UUID    id;

    @NotBlank
    private String  name;

    @NotBlank
    @Email
    private String  email;

    private String  password;
    private String  phoneNumber;
    private String  referralCode;
    private String  promoCode;

    @NotNull
    private Integer bonusPoints;

    private String  deviceInfo;
    private String  location;

    @NotBlank
    private String  userType;

    @NotNull
    private Boolean isActive;

    private Double  reviewScore;
    private String  reviewContext;
}
