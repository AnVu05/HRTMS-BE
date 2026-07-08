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

        @GetMapping("/doctors/{doctorId}/invitations")
        public ResponseEntity<ApiResponse<List<NotificationResponse>>> getDoctorInvitations(
                        @PathVariable Integer doctorId) {
                List<NotificationResponse> invitations = notificationService.getDoctorInvitations(doctorId);
                return ResponseEntity.ok(ApiResponse.success(
                                invitations,
                                "Fetched doctor invitations successfully"));
        }

        //Lay tat ca cac th.bao (theo nhieu loai) cua Admin (voi ID) - s.dung co che phan PAGE cua Spring
        //Vi th.bao danh cho ADMIN co k.nang se nhieu nen can phan trang
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
        public ResponseEntity<ApiResponse<List<NotificationResponse>>> markAllAdminNotificationsAsRead(@PathVariable Integer adminId) {
                List<NotificationResponse> responses = notificationService.markAllAdminNotificationsAsRead(adminId);
                return ResponseEntity.ok(ApiResponse.success(responses, "All notifications have been read"));
        }

        @PutMapping("/recipients/{recipientId}/read")
        public ResponseEntity<ApiResponse<List<NotificationResponse>>> markAllNotificationsAsRead(@PathVariable Integer recipientId) {
                List<NotificationResponse> responses = notificationService.markNotificationsAsReadByRecipient(recipientId);
                return ResponseEntity.ok(ApiResponse.success(responses, "Notifications have been read"));
        }

        //Doc thong bao cho Horse-Owner (a Khai)
        @GetMapping("/horse-owners/{ownerId}")
        public ResponseEntity<ApiResponse<List<HorseOwnerNotificationResponse>>> getHorseOwnerNotifications(
                        @PathVariable Integer ownerId) {
                List<HorseOwnerNotificationResponse> notifications = notificationService
                                .getHorseOwnerNotifications(ownerId);

                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched horse owner notifications successfully"));
        }

        //Lay tat ca loi moi dang cho xu li voi refereeId tuong ung (Thien)
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
        public ResponseEntity<ApiResponse<RefereeInvitationResponse>> respondToRefereeInvitation(
                        @PathVariable Integer refereeId,
                        @PathVariable Integer notificationId,
                        @RequestBody RespondInvitationRequest request) {
                // Thực thi việc cập nhật trạng thái đồng ý/từ chối của trọng tài đối với lời
                // mời cuộc đua tương ứng
                RefereeInvitationResponse response = notificationService.respondToRefereeInvitation(refereeId, notificationId, request);

                // Trả về kết quả thành công với dữ liệu đã cập nhật
                return ResponseEntity.ok(ApiResponse.success(
                                response,
                                "Responded to referee invitation successfully"));
        }

        //Lay tat ca th.bao cho Jockey voi jockeyId tuong ung
        @GetMapping("/jockeys/{jockeyId}")
        public ResponseEntity<ApiResponse<List<NotificationResponse>>> getJockeyNotifications(
                        @PathVariable Integer jockeyId) {
                List<NotificationResponse> notifications = notificationService.getJockeyNotifications(jockeyId);
                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched jockey notifications successfully"));
        }

        //Lay tat ca th.bao cho referee voi refereeId tuong ung
        @GetMapping("/referees/{refereeId}")
        public ResponseEntity<ApiResponse<List<NotificationResponse>>> getRefereeNotifications(
                        @PathVariable Integer refereeId) {
                List<NotificationResponse> notifications = notificationService.getRefereeNotifications(refereeId);
                return ResponseEntity.ok(ApiResponse.success(
                                notifications,
                                "Fetched referee notifications successfully"));
        }
}


