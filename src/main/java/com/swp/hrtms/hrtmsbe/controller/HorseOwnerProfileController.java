package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.HorseOwnerProfileUpdateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerProfileResponse;
import com.swp.hrtms.hrtmsbe.service.HorseOwnerProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Khải: Controller cung cấp API xem và cập nhật profile cho chủ ngựa.
@RestController
@RequestMapping("/api/horse-owners")
public class HorseOwnerProfileController {

    private final HorseOwnerProfileService horseOwnerProfileService;

    public HorseOwnerProfileController(HorseOwnerProfileService horseOwnerProfileService) {
        this.horseOwnerProfileService = horseOwnerProfileService;
    }

    // Khải: API view profile chủ ngựa theo ownerId.
    @GetMapping("/{ownerId}/profile")
    public ResponseEntity<ApiResponse<HorseOwnerProfileResponse>> getProfile(@PathVariable Integer ownerId) {
        HorseOwnerProfileResponse response = horseOwnerProfileService.getProfile(ownerId);
        return ResponseEntity.ok(ApiResponse.success(response, "Horse owner profile fetched successfully"));
    }

    // Khải: API update username, email, password của chủ ngựa theo ownerId.
    @PutMapping("/{ownerId}/profile")
    public ResponseEntity<ApiResponse<HorseOwnerProfileResponse>> updateProfile(
            @PathVariable Integer ownerId,
            @RequestBody HorseOwnerProfileUpdateRequest request) {
        HorseOwnerProfileResponse response = horseOwnerProfileService.updateProfile(ownerId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Horse owner profile updated successfully"));
    }
}
