package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertificateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;
import com.swp.hrtms.hrtmsbe.service.JockeyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
