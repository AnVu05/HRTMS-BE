package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertificateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;
import com.swp.hrtms.hrtmsbe.service.JockeyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jockeys")
@RequiredArgsConstructor
public class JockeyProfileController {

    private final JockeyProfileService jockeyProfileService;

    @GetMapping("/{jockeyId}/profile")
    public ResponseEntity<ApiResponse<JockeyProfileResponse>> getProfile(@PathVariable Integer jockeyId) {
        JockeyProfileResponse response = jockeyProfileService.getProfile(jockeyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Fetched jockey profile successfully"));
    }

    @PutMapping("/{jockeyId}/profile")
    public ResponseEntity<ApiResponse<JockeyProfileResponse>> updateProfile(
            @PathVariable Integer jockeyId,
            @RequestBody JockeyProfileUpdateRequest request) {
        JockeyProfileResponse response = jockeyProfileService.updateProfile(jockeyId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Updated jockey profile successfully"));
    }

    // Khai: API for a jockey to view all certificates and their current admin-updated statuses.
    @GetMapping("/{jockeyId}/certificates")
    public ResponseEntity<ApiResponse<List<JockeyCertificateResponse>>> getCertificates(
            @PathVariable Integer jockeyId) {
        List<JockeyCertificateResponse> response = jockeyProfileService.getCertificates(jockeyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Fetched jockey certificates successfully"));
    }

    // Khai: Update a certificate's name and image, then send it back to PENDING status.
    @PutMapping("/{jockeyId}/certificates/{certId}")
    public ResponseEntity<ApiResponse<JockeyCertificateResponse>> updateCertificate(
            @PathVariable Integer jockeyId,
            @PathVariable Integer certId,
            @RequestBody JockeyCertUpdateRequest request) {
        JockeyCertificateResponse response = jockeyProfileService
                .updateCertificate(jockeyId, certId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Certificate updated successfully"));
    }

    // Khai: Permanently delete a certificate and return only status and message.
    @DeleteMapping("/{jockeyId}/certificates/{certId}")
    public ResponseEntity<Map<String, String>> deleteCertificate(
            @PathVariable Integer jockeyId,
            @PathVariable Integer certId) {
        jockeyProfileService.deleteCertificate(jockeyId, certId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Certificate deleted successfully"));
    }
}


