package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.SpectatorAvatarUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.SpectatorProfileUpdateRequest;
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
    public ResponseEntity<SpectatorProfileResponse> getProfile(@PathVariable Integer spectatorId) {
        return ResponseEntity.ok(spectatorProfileService.getProfile(spectatorId));
    }

    @PutMapping("/{spectatorId}/profile")
    public ResponseEntity<SpectatorProfileResponse> updateProfile(
            @PathVariable Integer spectatorId,
            @RequestBody SpectatorProfileUpdateRequest request) {
        return ResponseEntity.ok(spectatorProfileService.updateProfile(spectatorId, request));
    }

    @PutMapping("/{spectatorId}/profile/avatar")
    public ResponseEntity<SpectatorProfileResponse> updateAvatar(
            @PathVariable Integer spectatorId,
            @RequestBody SpectatorAvatarUpdateRequest request) {
        return ResponseEntity.ok(spectatorProfileService.updateAvatar(spectatorId, request));
    }
}
