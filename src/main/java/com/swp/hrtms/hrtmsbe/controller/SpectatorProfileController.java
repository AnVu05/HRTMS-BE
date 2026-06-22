package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.SpectatorAvatarUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.SpectatorProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.SpectatorProfileResponse;
import com.swp.hrtms.hrtmsbe.service.SpectatorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spectators")
public class SpectatorProfileController {

    private final SpectatorProfileService spectatorProfileService;

    public SpectatorProfileController(SpectatorProfileService spectatorProfileService) {
        this.spectatorProfileService = spectatorProfileService;
    }

    @GetMapping("/{spectatorId}/profile")
    public ResponseEntity<ApiResponse<SpectatorProfileResponse>> getProfile(@PathVariable Integer spectatorId) {
        SpectatorProfileResponse response = spectatorProfileService.getProfile(spectatorId);
        return ResponseEntity.ok(ApiResponse.success(response, "Spectator profile fetched successfully"));
    }

    @PutMapping("/{spectatorId}/profile")
    public ResponseEntity<ApiResponse<SpectatorProfileResponse>> updateProfile(
            @PathVariable Integer spectatorId,
            @RequestBody SpectatorProfileUpdateRequest request) {
        SpectatorProfileResponse response = spectatorProfileService.updateProfile(spectatorId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Spectator profile updated successfully"));
    }

    @PutMapping("/{spectatorId}/profile/avatar")
    public ResponseEntity<ApiResponse<SpectatorProfileResponse>> updateAvatar(
            @PathVariable Integer spectatorId,
            @RequestBody SpectatorAvatarUpdateRequest request) {
        SpectatorProfileResponse response = spectatorProfileService.updateAvatar(spectatorId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Spectator avatar updated successfully"));
    }
}
