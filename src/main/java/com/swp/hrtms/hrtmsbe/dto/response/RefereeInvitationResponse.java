package com.swp.hrtms.hrtmsbe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefereeInvitationResponse {
    @com.fasterxml.jackson.annotation.JsonProperty("notification_id")
    private Integer notificationId; // Maps to Notification.id

    @com.fasterxml.jackson.annotation.JsonProperty("sender_id")
    private Integer senderId;

    private String title;
    private String content;
    private com.swp.hrtms.hrtmsbe.enums.NotificationType type;

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private java.time.LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonProperty("recipient_record_id")
    private Integer recipientRecordId; // Maps to NotificationRecipient.id

    @com.fasterxml.jackson.annotation.JsonProperty("recipient_id")
    private Integer recipientId;

    private com.swp.hrtms.hrtmsbe.enums.NotificationStatus status;

    @com.fasterxml.jackson.annotation.JsonProperty("read_at")
    private java.time.LocalDateTime readAt;

    @com.fasterxml.jackson.annotation.JsonProperty("race_id")
    private Integer raceId;

    @com.fasterxml.jackson.annotation.JsonProperty("race_name")
    private String raceName;

    @com.fasterxml.jackson.annotation.JsonProperty("tournament_name")
    private String tournamentName;

    private LocalDate date;

    @com.fasterxml.jackson.annotation.JsonProperty("start_time")
    private LocalTime startTime;

    @com.fasterxml.jackson.annotation.JsonProperty("end_time")
    private LocalTime endTime;

    @com.fasterxml.jackson.annotation.JsonProperty("distance_m")
    private Integer distanceM;
}
