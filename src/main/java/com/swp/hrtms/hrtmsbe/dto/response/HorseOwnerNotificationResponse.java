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
public class HorseOwnerNotificationResponse {
    private Integer notificationId;
    private String title;
    private String content;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
