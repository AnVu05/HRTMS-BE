package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.PredictionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.PredictionResponse;
import com.swp.hrtms.hrtmsbe.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@PreAuthorize("hasRole('SPECTATOR')") // BR_08: Eligibility requirements
public class PredictionController {
    private final PredictionService service;

    public PredictionController(PredictionService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ApiResponse<PredictionResponse>> create(@RequestBody PredictionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PredictionResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PredictionResponse>> update(@PathVariable Integer id, @RequestBody PredictionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        //khai
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


