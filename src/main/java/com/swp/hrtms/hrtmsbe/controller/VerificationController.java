package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/jockey-certs")
    public ResponseEntity<ApiResponse<List<JockeyVerificationRequestResponse>>> getJockeyVerificationRequests(
            @RequestParam("recipientId") Integer recipientId) {
        
        List<JockeyVerificationRequestResponse> responses = verificationService.getJockeyVerificationRequests(recipientId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Fetched verification requests successfully"));
    }

    @GetMapping("/jockey-certs/{jockeyId}/images")
    public ResponseEntity<ApiResponse<List<com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse>>> getPendingCertImages(
            @PathVariable("jockeyId") Integer jockeyId) {
        
        List<com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse> responses = verificationService.getPendingCertificateImages(jockeyId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Fetched certificate images successfully"));
    }

    // Khai: Save a Base64 certificate image directly as text with PENDING status.
    @PostMapping("/jockey-certs/{jockeyId}")
    public ResponseEntity<ApiResponse<Integer>> createJockeyCertificate(
            @PathVariable Integer jockeyId,
            @RequestBody JockeyCertCreateRequest request) {
        Integer certificateId = verificationService.createJockeyCertificate(jockeyId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(certificateId, "Certificate created successfully"));
    }

    // Khai: Create one verification notification and deliver it to every admin.
    @PostMapping("/jockey-certs/{jockeyId}/request-verification")
    public ResponseEntity<ApiResponse<Integer>> requestVerificationForAll(@PathVariable Integer jockeyId) {
        Integer notificationId = verificationService.requestVerificationForAll(jockeyId);
        return ResponseEntity.ok(
                ApiResponse.success(notificationId, "Certificate verification requested successfully"));
    }

    @PutMapping("/jockey-certs/{jockeyId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptJockeyCertificates(
            @PathVariable("jockeyId") Integer jockeyId,
            @RequestParam("adminId") Integer adminId) {

        verificationService.acceptJockeyCertificates(jockeyId, adminId);
        return ResponseEntity.ok(ApiResponse.success(null, "Certificates accepted successfully"));
    }

    @PutMapping("/jockey-certs/{jockeyId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectJockeyCertificates(
            @PathVariable("jockeyId") Integer jockeyId,
            @RequestParam("adminId") Integer adminId,
            @org.springframework.web.bind.annotation.RequestBody com.swp.hrtms.hrtmsbe.dto.request.RejectVerificationRequest request) {

        verificationService.rejectJockeyCertificates(jockeyId, adminId, request.getReason());
        return ResponseEntity.ok(ApiResponse.success(null, "Certificates rejected successfully"));
    }
}
