package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/jockeys/{jockeyId}/certificate-results")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getRecentCertificateNotifications(
            @PathVariable Integer jockeyId) {
        List<NotificationResponse> notifications =
                notificationService.getRecentCertificateNotifications(jockeyId);

        return ResponseEntity.ok(ApiResponse.success(
                notifications,
                "Fetched recent certificate notifications successfully"));
    }
}
