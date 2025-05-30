package com.smart_shopping_list_expense_manager.java.smart_shopping_list_expense_manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("sourceUserId")
    private UUID sourceUserId;
    
    @JsonProperty("sourceUserName")
    private String sourceUserName;
    
    @JsonProperty("sourceUserAvatar")
    private String sourceUserAvatar;
    
    @JsonProperty("destinationUserId")
    private UUID destinationUserId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("notificationType")
    private String notificationType;
    
    @JsonProperty("isRead")
    private Boolean isRead;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
} 