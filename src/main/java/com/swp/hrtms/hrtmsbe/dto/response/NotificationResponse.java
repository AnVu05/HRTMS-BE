package com.swp.hrtms.hrtmsbe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Integer id;
    
    @com.fasterxml.jackson.annotation.JsonProperty("sender_id")
    private Integer senderId;
    
    private String title;
    private String content;
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    private com.swp.hrtms.hrtmsbe.enums.NotificationType type;
    
    @com.fasterxml.jackson.annotation.JsonProperty("race_id")
    private Integer raceId;

    @com.fasterxml.jackson.annotation.JsonProperty("recipient_record_id")
    private Integer recipientRecordId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("recipient_id")
    private Integer recipientId;
    
    private com.swp.hrtms.hrtmsbe.enums.NotificationStatus status;
    
    @com.fasterxml.jackson.annotation.JsonProperty("read_at")
    private LocalDateTime readAt;
}

