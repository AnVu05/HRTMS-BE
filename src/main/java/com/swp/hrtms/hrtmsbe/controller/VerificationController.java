package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/verifications")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/jockey-certs")
    public ResponseEntity<ApiResponse<List<JockeyCert>>> getJockeyVerificationRequests(
            @RequestParam("recipientId") Integer recipientId) {
        
        List<JockeyCert> responses = verificationService.getJockeyVerificationRequests(recipientId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Fetched verification requests successfully"));
    }

    @GetMapping("/jockey-certs/{jockeyId}/images")
    public ResponseEntity<ApiResponse<List<JockeyCert>>> getPendingCertImages(
            @PathVariable("jockeyId") Integer jockeyId) {
        
        List<JockeyCert> responses = verificationService.getPendingCertificateImages(jockeyId);
        return ResponseEntity.ok(ApiResponse.success(responses, "Fetched certificate images successfully"));
    }

    // Khai: Save a certificate directly as entity data with PENDING status.
    @PostMapping("/jockey-certs")
    public ResponseEntity<ApiResponse<JockeyCert>> createJockeyCertificate(
            @RequestBody JockeyCertCreateRequest request) {
        JockeyCert certificate = verificationService.createJockeyCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(certificate, "Certificate created successfully"));
    }

    // Khai: Create one verification notification and deliver it to every admin.
    @PostMapping("/jockey-certs/{jockeyId}/request-verification")
    public ResponseEntity<Map<String, String>> requestVerificationForAll(@PathVariable Integer jockeyId) {
        verificationService.requestVerificationForAll(jockeyId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Certificate verification requested successfully"));
    }

    @PutMapping("/jockey-certs/{jockeyId}/accept")
    public ResponseEntity<ApiResponse<List<JockeyCert>>> acceptJockeyCertificates(
            @PathVariable("jockeyId") Integer jockeyId,
            @RequestParam("adminId") Integer adminId) {

        List<JockeyCert> certs = verificationService.acceptJockeyCertificates(jockeyId, adminId);
        return ResponseEntity.ok(ApiResponse.success(certs, "Certificates accepted successfully"));
    }

    @PutMapping("/jockey-certs/{jockeyId}/reject")
    public ResponseEntity<ApiResponse<List<JockeyCert>>> rejectJockeyCertificates(
            @PathVariable("jockeyId") Integer jockeyId,
            @RequestParam("adminId") Integer adminId,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.RejectVerificationRequest request) {

        List<JockeyCert> certs = verificationService.rejectJockeyCertificates(jockeyId, adminId, request.getReason());
        return ResponseEntity.ok(ApiResponse.success(certs, "Certificates rejected successfully"));
    }
}


