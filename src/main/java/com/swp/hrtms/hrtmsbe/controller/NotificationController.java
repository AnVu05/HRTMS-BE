package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerNotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;
import com.swp.hrtms.hrtmsbe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

        private final NotificationService notificationService;

        @GetMapping("/jockeys/{jockeyId}/certificate-results")
        public ResponseEntity<ApiResponse<List<NotificationResponse>>> getRecentCertificateNotifications(
                        @PathVariable Integer jockeyId) {
                List<NotificationResponse> notifications = notificationService
                                .getRecentCertificateNotifications(jockeyId);

                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched recent certificate notifications successfully"));
        }

        @GetMapping("/admin/{adminId}")
        public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAdminNotifications(
                        @PathVariable Integer adminId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        @RequestParam(defaultValue = "false") boolean unreadOnly) {
                Page<NotificationResponse> notifications = notificationService.getAdminNotifications(adminId, page,
                                size, unreadOnly);
                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched admin notifications successfully"));
        }

        @PutMapping("/admin/{adminId}/read")
        public ResponseEntity<ApiResponse<Void>> markAllAdminNotificationsAsRead(@PathVariable Integer adminId) {
                notificationService.markAllAdminNotificationsAsRead(adminId);
                return ResponseEntity.ok(ApiResponse.success(null, "All notifications have been read"));
        }

        @GetMapping("/horse-owners/{ownerId}")
        public ResponseEntity<ApiResponse<Page<HorseOwnerNotificationResponse>>> getHorseOwnerNotifications(
                        @PathVariable Integer ownerId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<HorseOwnerNotificationResponse> notifications = notificationService
                                .getHorseOwnerNotifications(ownerId, page, size);
                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched horse owner notifications successfully"));
        }

        @GetMapping("/jockeys/{jockeyId}")
        public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getJockeyNotifications(
                        @PathVariable Integer jockeyId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<NotificationResponse> notifications = notificationService
                                .getJockeyNotifications(jockeyId, page, size);

                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched jockey notifications successfully"));
        }

        @GetMapping("/referees/{refereeId}/invitations")
        public ResponseEntity<ApiResponse<List<RefereeInvitationResponse>>> getPendingRefereeInvitations(
                        @PathVariable Integer refereeId) {
                // Gọi Service lấy danh sách các lời mời trọng tài đang chờ xử lý
                List<RefereeInvitationResponse> invitations = notificationService
                                .getPendingRefereeInvitations(refereeId);

                // Trả về phản hồi thành công theo định dạng ApiResponse chuẩn của hệ thống
                return ResponseEntity.ok(ApiResponse.success(
                                invitations,
                                "Fetched pending referee invitations successfully"));
        }

        @PutMapping("/referees/{refereeId}/invitations/{notificationId}/respond")
        public ResponseEntity<ApiResponse<Void>> respondToRefereeInvitation(
                        @PathVariable Integer refereeId,
                        @PathVariable Integer notificationId,
                        @RequestBody RespondInvitationRequest request) {
                // Thực thi việc cập nhật trạng thái đồng ý/từ chối của trọng tài đối với lời
                // mời cuộc đua tương ứng
                notificationService.respondToRefereeInvitation(refereeId, notificationId, request);

                // Trả về kết quả thành công không có dữ liệu kèm theo (Void)
                return ResponseEntity.ok(ApiResponse.success(
                                null,
                                "Responded to referee invitation successfully"));
        }

        @GetMapping("/spectators/{spectatorId}")
        public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getSpectatorNotifications(
                        @PathVariable Integer spectatorId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<NotificationResponse> notifications = notificationService.getSpectatorNotifications(spectatorId,
                                page,
                                size);
                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched spectator notifications successfully"));
        }
}
