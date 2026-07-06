package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseOwnerNotificationResponse {
    @com.fasterxml.jackson.annotation.JsonProperty("notification_id")
    private Integer notificationId; // Maps to Notification.id
    
    @com.fasterxml.jackson.annotation.JsonProperty("sender_id")
    private Integer senderId;
    
    private String title;
    private String content;
    private com.swp.hrtms.hrtmsbe.enums.NotificationType type;
    
    @com.fasterxml.jackson.annotation.JsonProperty("race_id")
    private Integer raceId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @com.fasterxml.jackson.annotation.JsonProperty("recipient_record_id")
    private Integer recipientRecordId; // Maps to NotificationRecipient.id
    
    @com.fasterxml.jackson.annotation.JsonProperty("recipient_id")
    private Integer recipientId;
    
    private com.swp.hrtms.hrtmsbe.enums.NotificationStatus status;
    
    @com.fasterxml.jackson.annotation.JsonProperty("read_at")
    private LocalDateTime readAt;
}

