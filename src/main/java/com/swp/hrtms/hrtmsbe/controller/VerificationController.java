package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
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

    @PutMapping("/jockey-certs/{jockeyId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptJockeyCertificates(
            @PathVariable("jockeyId") Integer jockeyId,
            @RequestParam("adminId") Integer adminId) {

        verificationService.acceptJockeyCertificates(jockeyId, adminId);
        return ResponseEntity.ok(ApiResponse.success(null, "Certificates accepted successfully"));
    }
}
