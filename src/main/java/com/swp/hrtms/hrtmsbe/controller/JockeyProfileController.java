package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyProfileResponse;
import com.swp.hrtms.hrtmsbe.service.JockeyProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
