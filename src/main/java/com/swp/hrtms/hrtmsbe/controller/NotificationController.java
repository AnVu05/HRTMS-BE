package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;
import com.swp.hrtms.hrtmsbe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        List<NotificationResponse> notifications =
                notificationService.getRecentCertificateNotifications(jockeyId);

        return ResponseEntity.ok(ApiResponse.success(
                notifications,
                "Fetched recent certificate notifications successfully"));
    }

    @GetMapping("/referees/{refereeId}/invitations")
    public ResponseEntity<ApiResponse<List<RefereeInvitationResponse>>> getPendingRefereeInvitations(
            @PathVariable Integer refereeId) {
        // Gọi Service lấy danh sách các lời mời trọng tài đang chờ xử lý
        List<RefereeInvitationResponse> invitations =
                notificationService.getPendingRefereeInvitations(refereeId);

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
        // Thực thi việc cập nhật trạng thái đồng ý/từ chối của trọng tài đối với lời mời cuộc đua tương ứng
        notificationService.respondToRefereeInvitation(refereeId, notificationId, request);

        // Trả về kết quả thành công không có dữ liệu kèm theo (Void)
        return ResponseEntity.ok(ApiResponse.success(
                null,
                "Responded to referee invitation successfully"));
    }
}
