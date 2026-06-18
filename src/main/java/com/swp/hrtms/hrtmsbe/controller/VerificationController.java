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
}
