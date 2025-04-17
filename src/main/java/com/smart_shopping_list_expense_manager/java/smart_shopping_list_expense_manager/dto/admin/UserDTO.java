package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name is too long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Provide a valid email")
    @Size(max = 100)
    private String email;

    private String password;
    private String phoneNumber;
    private String referralCode;
    private String promoCode;

    @Min(value = 0, message = "Bonus points must be non-negative")
    private Integer bonusPoints;

    private String deviceInfo;
    private String location;
    private String userType;

    @NotNull(message = "Active flag must be provided")
    private Boolean isActive;

    private Double reviewScore;
    private String reviewContext;


}
