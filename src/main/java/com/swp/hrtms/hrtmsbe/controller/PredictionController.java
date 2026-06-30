package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.PredictionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.PredictionResponse;
import com.swp.hrtms.hrtmsbe.service.PredictionService;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/predictions")
@PreAuthorize("hasRole('SPECTATOR')")

public class PredictionController {
    private final PredictionService service;

    public PredictionController(PredictionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PredictionResponse>> create(@RequestBody PredictionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }
}
