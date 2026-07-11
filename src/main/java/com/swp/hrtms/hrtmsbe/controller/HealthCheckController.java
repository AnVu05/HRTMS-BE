package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckRequest;
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HealthCheckResponse;
import com.swp.hrtms.hrtmsbe.service.HealthCheckService;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/healthchecks")
public class HealthCheckController {
    private final HealthCheckService service;

    public HealthCheckController(HealthCheckService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HealthCheckResponse>> create(@RequestBody HealthCheckCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HealthCheckResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthCheckResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HealthCheckResponse>> update(@PathVariable Integer id,
            @RequestBody HealthCheckRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<HealthCheckResponse>>> getAssignedHealthChecks(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(ApiResponse.success(service.getAssignedHealthChecks(doctorId), "Fetched successfully"));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<ApiResponse<HealthCheckResponse>> processHealthCheck(
            @PathVariable Integer id,
            @RequestBody com.swp.hrtms.hrtmsbe.dto.request.HealthCheckProcessRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.processHealthCheck(id, request), "Processed successfully"));
    }
}

