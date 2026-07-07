package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.RaceResultRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResultResponse;
import com.swp.hrtms.hrtmsbe.service.RaceResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raceresults")
public class RaceResultController {
    private final RaceResultService service;

    public RaceResultController(RaceResultService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ApiResponse<RaceResultResponse>> create(@RequestBody RaceResultRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.create(request), "Created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RaceResultResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RaceResultResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RaceResultResponse>> update(@PathVariable Integer id, @RequestBody RaceResultRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.update(id, request), "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }
}


